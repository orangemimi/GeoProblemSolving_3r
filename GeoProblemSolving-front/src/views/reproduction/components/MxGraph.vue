<template>
  <div class>
    <div class="customToolbarContainer">
      <div class="toolbarContainer">
        <el-row>
          <all-tools
            ref="allTools"
            @getPersonalTools="getPersonalTools"
            @getPublicTools="getPublicTools"
          ></all-tools>
        </el-row>
      </div>
      <div class="mainContainer">
        <div class="toolbarTop">
          <!-- <el-button @click="saveGraph" type="text" size="mini">Output</el-button> -->
          <el-button @click="exportGraph" type="text" size="mini"
            >Export as XML</el-button
          >
          <!-- <input @change="readFile" ref="importInput" class="hide" type="file" />
          <el-button @click="importGraphFile" type="text" size="mini">Import mxGraph</el-button> -->
          <el-button
            @click="checked ? deleteCells() : deleteCellsConfirmDialog()"
            type="text"
            size="mini"
            :disabled="selectionCells.length == 0"
            >Delete</el-button
          >
          <el-button @click="undo" type="text" size="mini">Undo</el-button>
          <el-button @click="redo" type="text" size="mini">Redo</el-button>
          <el-button @click="saveGraph" type="success" size="mini"
            >Save Task</el-button
          >
          <!-- <el-button @click="saveGraph" size="mini"> checkcell</el-button> -->
        </div>
        <vue-scroll style="height: 630px; width: 100%">
          <div class="graphContainer" ref="container"></div>
        </vue-scroll>

        <!-- <div class="outline-wrapper">
          <h4>导航器</h4>
          <div id="graphOutline" />
        </div> -->
      </div>

      <div class="editCellContainer">
        <!-- <edit-cell :visible="editCellVisible" @currentGraph="grapg"></edit-cell> -->
        <div v-if="toolClick" class="normalContaniner">
          <div>Node Info</div>
          <!-- <vue-scroll style="height: 630px; width: 100%">
            <data-item-info :cell="dataNode"></data-item-info>
          </vue-scroll> -->
        </div>
        <div v-show="toolDoubleClick" class="normalContaniner">
          <div>Node Info</div>
          <vue-scroll style="height: 630px; width: 100%">
            <data-item-toolbar
              :cell="cell"
              ref="dataItem"
              @getInAndOut="getInAndOut"
              :key="dataItemToolbarKey"
            ></data-item-toolbar>
          </vue-scroll>
        </div>
        <div v-show="dataClick" class="expandContaniner">
          <div>Node Info</div>
          <data-item-info :cell="dataNode"></data-item-info>
        </div>
        <div v-if="dataDoubleClick">
          {{ dataNode }}
          <div>Node Info</div>
          <data-item-info :cell="dataNode"></data-item-info>
        </div>
      </div>

      <div class="dialogs">
        <el-dialog :visible.sync="deleteCellsVisible" width="30%">
          <span>Are you sure to delete this node?</span>
          <span slot="footer" class="dialog-footer">
            <el-checkbox v-model="checked" label="1"
              >Ignore this tip in this action</el-checkbox
            >
            <el-button @click="deleteCellsVisible = false">Cancel</el-button>
            <el-button type="primary" @click="deleteCells">Confirm</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import mxgraph from "@/components/utils/mxGraph/index";
import nodeCard from "@/components/utils/mxGraph/components/nodeCard";
import editCell from "@/components/utils/mxGraph/components/editCell";
import FileSaver from "file-saver";
import allTools from "@/components/r2/AllTools";
import { get } from "@/axios";
import dataItemToolbar from "./DataItemToolbar";
import dataItemInfo from "./DataItemInfo";

const {
  mxGraph,
  mxOutline,
  mxEvent,
  mxCell,
  mxGeometry,
  mxUtils,
  mxEventObject,
  mxConnectionHandler,
  mxGraphHandler,
  mxRubberband,
  mxConstants,
  mxCellState,
  mxCellEditor,
  mxGraphView,
  mxCodec,
  mxUndoManager,
} = mxgraph;

export default {
  props: {
    sendXml: {
      type: String,
    },
  },
  components: { nodeCard, editCell, allTools, dataItemToolbar, dataItemInfo },
  watch: {
    sendXml: {
      handler(val) {
        if (val != "") {
          this.getXml = val;
          this.importGraph(this.getXml);
        }
      },
      deep: true,
    },
  },

  computed: {},

  data() {
    return {
      contentHeight: 0,
      graph: null,
      editCellVisible: false,
      graphXml: "",
      selectEdge: {},
      selectVertex: {},
      selectionCells: [],
      deleteCellsVisible: false,
      checked: false,
      undoMng: null,

      cellForm: {
        name: "",
      },
      cellFormRules: {
        name: [
          {
            required: true,
            message: "The name cannot be empty",
            trigger: "blur",
          },
        ],
      },
      getXml: this.sendXml,
      //toolbar
      toolItemList: [],
      publicTools: [],
      personalTools: [],
      userInfo: this.$store.getters.userInfo,
      doi: "",
      cell: {}, //双击事件 cell
      state: {},
      inputItemList: [],
      outputItemList: [],
      mxGraphSelectionModel: {},
      nodeActiveName: "info",
      dataNode: {},

      toolClick: false,
      toolDoubleClick: false,
      dataClick: false,
      dataDoubleClick: false,
      toolListInGraph: [],
      dataInputInGraph: [],
      dataLinkInGraph: [], //下一模型的输入数据
      dataOutputInGraph: [],
      linkEdgeList: [],

      stateList: [],
      dataItemToolbarKey: 0,
    };
  },

  methods: {
    async init() {
      this.initSize();
      this.createGraph();
      this.initConnectStyle();
      this.initGraph();

      this.initUndoManager();

      this.setDefaultEdgeStyle();
      this.listenGraphEvent();

      this.getGraphXml();

      this.initToolbar("toolItemList");
    },

    getPublicTools(val) {
      this.$set(this, "publicTools", val);
    },

    getPersonalTools(val) {
      this.$set(this, "personalTools", val);
      this.toolItemList = this.personalTools;
    },

    getInAndOut(input, output) {
      // this.state = val;
      this.inputItemList = input;
      this.outputItemList = output;
      this.initToolbar("inputItemList");
      this.initToolbar("outputItemList");
    },

    initSize() {
      this.contentHeight = window.innerHeight - 310;
      // console.log(this.contentHeight);
    },

    createGraph() {
      this.graph = new mxGraph(this.$refs.container);
    },

    initGraph() {
      this.graph.convertValueToString = (cell) => {
        // 从value中获取显示的内容
        return cell.name;
      };

      let outline = new mxOutline(
        this.graph,
        document.getElementById("graphOutline")
      );

      this.setCursor();
    },
    setCursor() {},

    listenGraphEvent() {
      // 监听双击事件
      this.graph.addListener(mxEvent.DOUBLE_CLICK, async (graph, evt) => {
        // DOUBLE_CLICK
        const cell = evt.properties.cell;
        if (!cell) {
          return;
        }
        const clickToolType = cell.style.includes("toolType");
        const dataType =
          cell.style.includes("dataInputType") ||
          cell.style.includes("dataOutputType");
        if (clickToolType) {
          this.dataItemToolbarKey++;

          await this.$refs.dataItem.initSetTimeOut();
          this.toolDoubleClick = true;
          this.inputItemList = this.outputItemList = [];
          this.cell = cell;

          this.dataDoubleClick = this.dataClick = this.toolClick = false;
        } else if (dataType) {
          this.dataNode = cell;
          this.dataDoubleClick = true;
          this.toolDoubleClick = this.toolClick = this.dataClick = false;
        }
      });

      // 监听单击事件
      //单击空白处 dialog隐藏
      this.graph.addListener(mxEvent.CLICK, (sender, evt) => {
        const cell = evt.properties.cell;
        if (!cell) {
          return;
        }
        const clickToolType = cell.style.includes("toolType");
        const dataType =
          cell.style.includes("dataInputType") ||
          cell.style.includes("dataOutputType");
        if (clickToolType) {
          // 使用 mxGraph 事件中心，触发自定义事件
          // this.cell = cell;
          this.toolClick = true;
          this.toolDoubleClick = this.dataClick = this.dataDoubleClick = false;
        } else if (dataType) {
          this.dataNode = cell;
          // console.log(this.dataNode, dataType);
          this.dataClick = true;
          this.toolDoubleClick = this.toolClick = this.dataDoubleClick = false;
        }

        // if (evt.properties.hasOwnProperty("cell")) {
        //   this.cell = evt.properties.cell;
        //   // this.getCellInfo(this.cell);
        // } else {
        //   if (sender.isMouseDown == false) {
        //     // this.cell = {};
        //     return;
        //   }
        // }
      });

      //连接线
      // this.graph.addListener(mxEvent.CONNECT_CELL, (sender, evt) => {
      //   // const cell = evt.properties.cell;
      //   console.log(11111111111);
      // });

      // 监听 mxGraph 事件
      this.mxGraphSelectionModel = this.graph.getSelectionModel();
      this.mxGraphSelectionModel.addListener(
        mxEvent.CHANGE,
        this.handleSelectionChange
      );
    },

    initConnectStyle() {
      //允许连线
      this.graph.setConnectable(true);

      // 编辑时按回车键不换行，而是完成输入
      this.graph.setEnterStopsCellEditing(true);

      // 可修改
      this.graph.setCellsEditable(true);

      // 从工具栏拖动到目标细胞时细胞边界是否产生光圈
      this.graph.setDropEnabled(true);

      // 禁止游离线条
      this.graph.setAllowDanglingEdges(false);
      this.graph.setDisconnectOnMove(false);

      this.graph.setConnectableEdges(false);

      //设置线条弯曲程度
      this.graph.setCellsBendable(true);

      // 禁止节点折叠
      this.graph.foldingEnabled = false;

      // 文本包裹效果必须开启此配置
      this.graph.setHtmlLabels(true);

      // 拖拽过程对齐线
      this.graph.graphHandler.guidesEnabled = true;

      // 容器大小自适应
      //   this.graph.setResizeContainer(true);

      // 重复连接
      this.graph.setMultigraph(true);

      // Enables rubberband selection
      new mxRubberband(this.graph);

      mxCellEditor.prototype.blurEnabled = true;

      //失焦
      //   if(this.graph.)
    },

    //设置为折线
    setDefaultEdgeStyle() {
      const style = this.graph.getStylesheet().getDefaultEdgeStyle();
      Object.assign(style, {
        [mxConstants.STYLE_ROUNDED]: true, // 设置线条拐弯处为圆角
        [mxConstants.STYLE_STROKEWIDTH]: "1",
        [mxConstants.STYLE_STROKECOLOR]: "#333333",
        [mxConstants.STYLE_EDGE]: mxConstants.EDGESTYLE_ORTHOGONAL,
        [mxConstants.STYLE_FONTCOLOR]: "#333333",
        [mxConstants.STYLE_LABEL_BACKGROUNDCOLOR]: "#ffa94d",
      });

      // 设置拖拽线的过程出现折线，默认为直线
      this.graph.connectionHandler.createEdgeState = () => {
        const edge = this.graph.createEdge();
        return new mxCellState(
          this.graph.view,
          edge,
          this.graph.getCellStyle(edge)
        );
      };
    },

    initToolbar(panel) {
      let refType;
      let listName = [];
      let styleIn = {};
      if (panel == "toolItemList") {
        refType = "allTools";
        listName = this.toolItemList;
        styleIn = {
          toolType: "",
          fillColor: "#f8f5ec",
          shape: "rectangle",
        };
      } else if (panel == "inputItemList") {
        refType = "dataItem";
        listName = this.inputItemList;
        styleIn = {
          dataInputType: "",
          fillColor: "#fff8f8",
          shape: "rectangle",
        };
      } else if (panel == "outputItemList") {
        refType = "dataItem";
        listName = this.outputItemList;
        styleIn = {
          dataOutputType: "",
          fillColor: "#f0f8ff",
          shape: "rectangle",
        };
      }
      const domArray = this.$refs[refType].$refs[panel];

      if (!(domArray instanceof Array) || domArray.length <= 0) {
        return;
      }
      // console.log(listName);
      // console.log(domArray);
      domArray.forEach((dom, domIndex) => {
        const toolItem = listName[domIndex];

        const dropHandler = (graph, evt, cell, x, y) => {
          this.addCell(toolItem, x, y, panel, styleIn);
        };

        const createDragPreview = () => {
          const elt = document.createElement("div");
          elt.style.border = "2px dotted black";
          elt.style.width = `200px`;
          elt.style.height = `50px`;
          return elt;
        };

        mxUtils.makeDraggable(
          dom,
          this.graph,
          dropHandler,
          createDragPreview(),
          0,
          0,
          false,
          true
        );
      });
    },

    addCell(item, x, y, type, styleIn) {
      // console.log(item);
      let styleObj = {
        ...styleIn,
        strokeColor: "rgb(200, 200, 200)",
        strokeWidth: "1.5",
        shape: "rectangle",
        align: mxConstants.ALIGN_CENTER,
        // verticalAlign: mxConstants.ALIGN_,
        imageAlign: mxConstants.ALIGN_CENTER,
        imageVerticalAlign: mxConstants.ALIGN_TOP,
      };
      if (
        item.type == "response" &&
        (item.optional == "False" || item.optional == "false")
      ) {
        styleObj.strokeColor = "#d13030";
      }

      this.graph.getModel().beginUpdate();

      try {
        if (type == "toolItemList") {
          let vertex = this.addCellToContainer(styleObj, x, y);
          vertex.name = item.toolName;
          vertex.doi = item.doi;
          vertex.step = "1";
          vertex.iterationNum = "1";
        } else {
          let selectionCell = this.selectionCells[0];
          // console.log(selectionCell.md5, item.md5);
          // if (selectionCell.md5 != item.md5) {
          //   return;
          // }
          let vertex = this.addCellToContainer(styleObj, x, y);

          vertex.name = item.name;
          vertex.eventId = item.eventId;
          vertex.stateName = item.stateName;
          vertex.md5 = item.md5;

          if (type == "inputItemList") {
            this.addEdge(vertex, selectionCell);
          } else if (type == "outputItemList") {
            this.addEdge(selectionCell, vertex);
          }
        }
      } finally {
        this.graph.getModel().endUpdate();
      }
    },

    addCellToContainer(styleObj, x, y) {
      const style = Object.keys(styleObj)
        .map((attr) => `${attr}=${styleObj[attr]}`)
        .join(";");

      return this.graph.insertVertex(
        this.graph.getDefaultParent(),
        null,
        null,
        x,
        y,
        200, //width
        50, //height
        style
      );
    },

    addEdge(source, target) {
      this.graph.insertEdge(
        this.graph.getDefaultParent(),
        null,
        null,
        source,
        target,
        null
      );
    },

    getGraphXml() {
      let encoder = new mxCodec();
      this.graphXml = encoder.encode(this.graph.getModel());
      // console.log(this.graphXml, this.graph.getModel());
    },

    exportGraph() {
      this.getGraphXml();
      let xml = mxUtils.getPrettyXml(this.graphXml);
      const blob = new Blob([xml], {
        type: "text/plain;charset=utf-8",
      });
      FileSaver.saveAs(blob, "mxgraph.xml");
    },

    importGraphFile(evt) {
      this.$refs["importInput"].click();
    },
    readFile(evt) {
      const file = evt.target.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        const txt = e.target.result;
        this.importGraph(txt);
      };
      reader.readAsText(file);
    },

    importGraph(xmlTxt) {
      //xml to json
      this.graph.getModel().beginUpdate();
      try {
        const doc = mxUtils.parseXml(xmlTxt);
        const root = doc.documentElement;
        const dec = new mxCodec(root.ownerDocument);
        dec.decode(root, this.graph.getModel());
      } finally {
        this.graph.getModel().endUpdate();
      }
      this.restoreModel();
    },

    restoreModel() {
      Object.values(this.graph.getModel().cells).forEach((cell) => {
        if (cell.vertex && cell.data) {
          cell.data = JSON.parse(cell.data);
        }
      });
    },

    closeDialog(val) {
      this.editCellVisible = val;
    },

    submitCellForm(form) {
      this.$refs[form].validate((valid) => {
        if (valid) {
          this.graph.getModel().beginUpdate();
          let cell = this.graph.getSelectionCell();
          if (cell.name == this.cellForm.name) {
            this.$message({
              message: "Name changed is as same as before",
              type: "warning",
            });
          } else {
            try {
              cell.name = this.cellForm.name;
              this.graph.refresh(cell); // 刷新cell
              this.$message({
                message: "Refresh the node successfully!",
                type: "success",
              });
            } finally {
              this.graph.getModel().endUpdate();
            }
          }
        }
      });
    },

    handleSelectionChange(selectModel) {
      this.selectionCells = [];
      if (!selectModel.cells.length) {
        return;
      }
      this.selectionCells = this.graph.getSelectionCells();
    },

    deleteCellsConfirmDialog() {
      this.deleteCellsVisible = true;
    },

    deleteCells() {
      let cells = this.selectionCells;
      let tmpSet = new Set(this.selectionCells);

      cells.forEach((cell) => {
        this.findDeleteCell(cell, tmpSet);
      });
      this.deleteCellsVisible = false;

      this.graph.removeCells(Array.from(tmpSet), true);
    },

    findDeleteCell(cell, deleteSet) {
      deleteSet.add(cell);
      if (cell.edges) {
        cell.edges.forEach((tmpEdge) => {
          if (tmpEdge.target !== cell) {
            deleteSet.add(tmpEdge.target);
            this.findDeleteCell(tmpEdge.target, deleteSet);
          }
        });
      }
    },

    initUndoManager() {
      this.undoMng = new mxUndoManager();

      let listen = (sender, evt) => {
        this.undoMng.undoableEditHappened(evt.getProperty("edit"));
      };

      this.graph.getModel().addListener(mxEvent.UNDO, listen);
      this.graph.getView().addListener(mxEvent.UNDO, listen);
    },

    getUndoRedoCell() {
      let cells = [];
      if (this.undoMng) {
        let undoIndex = this.undoMng.indexOfNextAdd - 1;

        if (this.undoMng.history[undoIndex]) {
          cells = this.undoMng.history[undoIndex].changes.map((change) => {
            if (change.child) {
              return change.child;
            } else {
              return change.cell;
            }
          });
        }
      }
      return cells;
    },
    //撤销
    undo() {
      if (!this.undoMng) {
        throw new Error("mxUndoManager 没有初始化");
      }
      // console.info("后退的Cells", this.getUndoRedoCell());
      this.undoMng.undo();
    },
    //重做
    redo() {
      if (!this.undoMng) {
        throw new Error("mxUndoManager 没有初始化");
      }
      this.undoMng.redo();
      // console.info("前进的Cells", this.getUndoRedoCell());
    },

    //保存task
    async saveGraph() {
      if (this.graph.getModel().cells.length < 1) {
        this.$message.error("Please select at least one model.");
        return;
      }
      await this.getCells();
      this.generateXml();
    },

    async getCells() {
      // this.toolListInGraph = this.dataOutputInGraph = this.dataInputInGraph = this.dataLinkInGraph = [];
      let toolListInGraph = [];
      let dataOutputInGraph = [];
      let dataInputInGraph = [];
      let dataLinkInGraph = [];
      let dataLineInputInGrapg = [];

      Object.values(this.graph.getModel().cells).forEach((cell) => {
        if (cell.style != undefined) {
          if (cell.style.includes("toolType")) {
            toolListInGraph.push(cell);
          } else if (cell.style.includes("dataOutputType")) {
            dataOutputInGraph.push(cell);
          } else if (cell.style.includes("dataInputType")) {
            cell.type = "url";
            dataInputInGraph.push(cell);
          }
        }
      });

      let links = Object.values(this.graph.getModel().cells).filter(
        (cell) =>
          !cell.hasOwnProperty("vertex") &&
          cell.hasOwnProperty("source") &&
          cell.source.style.includes("dataOutputType")
      );
      this.linkEdgeList = links;

      links.forEach((link) => {
        dataInputInGraph.forEach((data, index) => {
          if (data == link.target) {
            data.type = "link";
          }
        });
      });

      await Promise.all(
        toolListInGraph.map(async (tool) => {
          let data = await get(
            `/GeoProblemSolving/modelTask/ModelBehaviorOrdinary/${tool.doi}`
          );
          tool.md5 = data.md5;
        })
      );

      this.toolListInGraph = toolListInGraph;
      this.dataOutputInGraph = dataOutputInGraph;
      this.dataInputInGraph = dataInputInGraph;
      this.dataLinkInGraph = dataLinkInGraph;

      console.log(toolListInGraph, dataInputInGraph, dataOutputInGraph);
    },

    generateXml() {
      let version = "1.0";
      let uid = this.generateGUID();
      let dataLinks = [];
      let xml = "";
      let name = "testIntegrateModel";

      xml += `<TaskConfiguration uid='${uid}' name='${name}' version='${version}'>`;

      xml += "<Models>";

      //没有md5-->只有doi       xml += `<Model name='${model.name}' pid='${model.md5}' description='' doi='${model.doi}'/>`;
      this.toolListInGraph.forEach((model) => {
        xml += `<Model name='${model.name}' description='' pid='${model.md5}'/>`;
      });
      xml += `</Models>`;

      //modelAction标签
      xml += `<ModelActions>`;

      this.toolListInGraph.forEach((tool) => {
        xml += `<ModelAction id='${tool.id}' name = '${tool.name}' description = '' 
        model='${tool.md5} ' step ='${tool.step}' iterationNum='${tool.iterationNum}'>`;

        let inputList = this.dataInputInGraph.filter(
          (event) => event.md5 == tool.md5
        );
        let outputList = this.dataOutputInGraph.filter(
          (event) => event.md5 == tool.md5
        );

        xml += `<Inputs>`;
        inputList.forEach((item) => {
          xml += `<DataConfiguration id='${item.eventId}' state='${item.stateName}' event='${item.name}'>`;

          if (item.type == "url") {
            xml += `<Data value='${item.url}' type="${item.type}"/>`;
          } else if (item.type == "link") {
            let link = this.linkEdgeList.filter(
              (el) => el.target.eventId == item.eventId
            );
            xml += `<Data link='${link[0].target.eventId}' type="${item.type}"/>`;
          }
          xml += `</DataConfiguration>`;
        });
        xml += `</Inputs>`;
        xml += `<Outputs>`;

        outputList.forEach((item) => {
          xml += `<DataConfiguration id='${item.eventId}' state='${item.stateName}' event='${item.name}' />`;
        });

        xml += "</Outputs></ModelAction>";
      });
      xml += "</ModelActions>";

      //data links标签
      xml += "<DataLinks>";
      this.linkEdgeList.forEach((item) => {
        xml += `<DataLink from='${item.source.eventId}'  to='${item.target.eventId}'  tool='' config='' />`;
      });
      xml += "</DataLinks></TaskConfiguration>";
      console.log(xml);
    },

    getInputOutputInXml() {},

    generateGUID() {
      let s = [];
      let hexDigits = "0123456789abcdef";
      for (let i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
      }
      s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
      s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
      s[8] = s[13] = s[18] = s[23] = "-";

      let uuid = s.join("");
      return uuid;
    },
  },
  async mounted() {
    await this.$refs.allTools.initSetTimeOut();
    await this.init();
  },
  created() {
    this.initSize();
  },
};
</script>

<style lang='scss' scoped>
.customToolbarContainer {
  width: 100%;
  height: 100%;
  display: flex;
  position: relative;

  .toolbarContainer {
    position: relative;
    width: 160px;
    // margin: 0 5px;
    // background-color: rgb(251, 251, 251);
    .toolbarTitle {
      font-size: 20px;
      font-weight: 600;
      text-align: center;
      margin: 5px 0;
    }

    .items {
      margin-top: 2px;
      .item {
        margin: 0 10px;
        width: 120px;
        text-align: center;
        margin-bottom: 5px;
      }
    }
  }

  .editCellContainer {
    position: relative;
    right: 0;
    width: 280px;
    padding: 0 5px;
    // z-index: 999;
    // .normalContaniner {
    //   width: 200px;
    // }

    // .expandContaniner {
    //   width: 262px;
    // }
  }

  .mainContainer {
    width: 990px;
    .toolbarTop {
      background: rgb(251, 251, 251);
      padding-left: 10px;
      border-radius: 4px;
      margin-bottom: 10px;
    }
    .graphContainer {
      overflow: hidden;
      height: 100%;
      width: 100%;
      min-width: 930px;
      min-height: 630px;
      background: rgb(251, 251, 251)
        url("./../../../assets/images/mxgraph/grid.gif") 0 0 repeat;
      border-radius: 4px;
    }
  }

  .outline-wrapper {
    border: 1px solid #dedede;
    background: #fff;
    position: fixed;
    right: 30px;
    bottom: 40px;
    border-radius: 4px;
    z-index: 10;
    > h4 {
      padding: 6px;
      font-size: 12px;
      border-bottom: 1px solid #e6e6e6;
    }
    #graphOutline {
      width: 200px;
    }
  }
}
</style>