<template>
  <div class="main">
    <div class="mainContainerMask" v-show="isNewTaskContainerShow">
      <div class="taskInfoForm">
        <el-form ref="taskInfoForm" :model="taskInfo" label-width="180px">
          <el-form-item label="Task Name">
            <el-input v-model="taskInfo.taskName"></el-input>
          </el-form-item>
          <el-form-item label="Task Description">
            <el-input v-model="taskInfo.taskDescription"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <!-- 进入，如果没有task，则弹出创建一个新画布 -->
      <div class="taskInfoBtn">
        <el-button @click="createNewTask" class="createTask"
          >Create a New Task</el-button
        >
        <div class="or">or</div>
        <integrate-tasks
          @selectTask="selectTask"
          class="selectTask"
        ></integrate-tasks>
        <div class="newTaskBtnTip">
          Please Enter the task name and description to create your task!
        </div>
      </div>
    </div>
    <div class="customToolbarContainer" v-show="!isNewTaskContainerShow">
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
          <!-- <el-button @click="updateGraph" type="text" size="mini">Output</el-button> -->
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
          <!-- 创建一个新的画布  均为create-->
          <el-button @click="showCreateNewTask" type="success" size="mini"
            >New Task</el-button
          >
          <!-- 保存现有的画布 均为update-->
          <el-button @click="updateGraph" type="warning" size="mini"
            >Save Task</el-button
          >
          <el-button @click="runGraph" size="mini"> Run Task</el-button>
          <integrate-tasks
            @selectTask="selectTask"
            style="float: left"
          ></integrate-tasks>
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
          <data-item-info
            :cell="dataNode"
            @currentEventWithFile="currentEventWithFile"
          ></data-item-info>
        </div>
        <div v-show="dataDoubleClick">
          <div>Node Info</div>
          <!-- <data-item-info :cell="dataNode"></data-item-info> -->
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

// import { Graph } from "@/components/utils/mxGraph/graph";
import nodeCard from "@/components/utils/mxGraph/components/nodeCard";
import editCell from "@/components/utils/mxGraph/components/editCell";
import FileSaver from "file-saver";

import allTools from "@/components/r2/AllTools";
import { get, post, patch } from "@/axios";
import dataItemToolbar from "./DataItemToolbar";
import dataItemInfo from "./DataItemInfo";

import integrateTasks from "./IntegrateTasks";
import IntegrateTasks from "./IntegrateTasks.vue";

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
  mxCellOverlay,
} = mxgraph;

export default {
  props: {
    sendXml: {
      type: String,
    },
  },

  components: {
    nodeCard,
    editCell,
    allTools,
    dataItemToolbar,
    dataItemInfo,
    integrateTasks,
  },
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
      userInfo: this.$store.getters.userInfo,
      projectId: this.$route.params.projectId,
      contentHeight: 0,
      graph: null,
      editCellVisible: false,
      // graphXml: "",
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
      taskInfo: {
        taskName: "",
        taskDescription: "",
      },

      isNewTaskContainerShow: true,

      currentTask: {},

      //document
      nodeList: [],
      modelItemList: [],
      dataItemList: [],

      //task
      taskList: [],
    };
  },

  methods: {
    async init() {
      await this.getIntegrateTasks();
      await this.getMethods();
      this.initSize();
      this.createGraph();
      this.initConnectStyle();
      this.initGraph();

      this.initUndoManager();

      this.setDefaultEdgeStyle();
      this.listenGraphEvent();

      this.getGraphXml();

      this.initToolbar("toolItemList");
      // this.initPopupMenu();
    },

    getPublicTools(val) {
      this.$set(this, "publicTools", val);
    },

    getPersonalTools(val) {
      this.$set(this, "personalTools", val);
      this.toolItemList = this.personalTools;
    },

    async getIntegrateTasks() {
      let data = await get(
        `/GeoProblemSolving/r/integrateTasks/all/${this.projectId}`
      ); //获得该项目的所有tasks
      if (data != null) {
        this.nodeList = data.nodeList;
        this.modelItemList = data.modelItemList;
        this.dataItemList = data.dataItemList;
      }
    },

    async getMethods() {
      let data = await get(`/GeoProblemSolving/r/methods/${this.projectId}`); //获得该项目的所有tasks
      if (data != null) {
        this.taskList = data;
      }
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
    initPopupMenu() {
      // console.log(document.body);
      mxEvent.disableContextMenu(document.body);
      this.graph.popupMenuHandler.autoExpand = true;
    },

    listenGraphEvent() {
      // 监听双击事件
      this.graph.addListener(mxEvent.DOUBLE_CLICK, async (graph, evt) => {
        // DOUBLE_CLICK
        let cell = evt.properties.cell;
        if (!cell) {
          return;
        }
        let clickToolType = cell.style.includes("toolType");
        let dataType = cell.style.includes("data");
        if (clickToolType) {
          this.dataItemToolbarKey++;
          // console.log(this.dataItemToolbarKey);
          await this.$refs.dataItem.initSetTimeOut();
          this.toolDoubleClick = true;
          this.cell = cell;
          this.dataDoubleClick = this.dataClick = this.toolClick = false;
        }
        // else if (dataType) {
        //   this.dataDoubleClick = true;
        //   this.dataNode = cell;
        //   this.toolDoubleClick = this.toolClick = this.dataClick = false;
        // }
      });

      // 监听单击事件
      //单击空白处 dialog隐藏
      this.graph.addListener(mxEvent.CLICK, (sender, evt) => {
        let cell = evt.properties.cell;
        if (!cell) {
          return;
        }
        const clickToolType = cell.style.includes("toolType");
        const dataType = cell.style.includes("data");
        if (clickToolType) {
          // 使用 mxGraph 事件中心，触发自定义事件
          // this.cell = cell;
          this.toolClick = true;
          this.toolDoubleClick = this.dataClick = this.dataDoubleClick = false;
        } else if (dataType) {
          this.toolDoubleClick = this.toolClick = this.dataDoubleClick = false;
          this.dataClick = true;
          // console.log(cell);
          this.dataNode = cell;
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
      //
      // this.mxEvent.disableContextMenu(this.document.body);

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
          // vertex.step = "1";
          vertex.iterationNum = "1";
        } else {
          let selectionCell = this.selectionCells[0];
          let vertex = this.addCellToContainer(styleObj, x, y);

          vertex.name = item.name;
          vertex.eventId = item.eventId;
          vertex.stateName = item.stateName;
          vertex.md5 = item.md5;
          vertex.doi = item.doi;
          vertex.optional = item.optional;

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
      let graphXml = encoder.encode(this.graph.getModel());
      let xml = mxUtils.getPrettyXml(graphXml);
      // console.log(xml);
      return xml;
      // console.log(this.graphXml, this.graph.getModel());
    },

    exportGraph() {
      let graphXml = this.getGraphXml();
      // console.log(graphXml);
      let xml = mxUtils.getPrettyXml(graphXml);
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

    //创建一个新的画布
    showCreateNewTask() {
      this.taskInfo = {
        taskName: "",
        taskDescription: "",
      };
      this.isNewTaskContainerShow = true;
    },

    async createNewTask() {
      let postJson = {
        projectId: this.projectId,
        userName: this.userInfo.userName,
        userId: this.userInfo.userId,
        ...this.taskInfo,
      };
      // console.log(postJson);
      let data = await post(`/GeoProblemSolving/r/integrateTasks`, postJson);
      this.currentTask = data;
      this.isNewTaskContainerShow = false;
      // console.log(data);
    },

    //保存task
    async updateGraph() {
      if (this.graph.getModel().cells.length < 1) {
        this.$message.error("Please select at least one model.");
        return;
      }
      await this.getCells();
      await this.getProcessDoc();
      let xml = this.generateXml();
      let graphXml = this.getGraphXml();
      let modelActions = this.generateTaskModelInfo();
      let postJson = {
        xml: xml,
        mxgraph: graphXml,
        // modelActions: modelActions,modelACTIONlIST
        taskName: this.taskInfo.taskName,
        taskDescription: this.taskInfo.taskDescription,
      };
      // console.log(postJson);
      let data = await patch(
        `/GeoProblemSolving/r/integrateTasks/${this.currentTask.id}`,
        postJson
      );
      // console.log(data);
    },

    selectTask(val) {
      this.currentTask = val;
      this.taskInfo = val;
      let mxgraph = val.mxgraph;
      this.importModelXML(mxgraph);
      this.isNewTaskContainerShow = false;
      // console.log(val);
    },

    importModelXML(xmlTxt) {
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
      // this._restoreModel();
    },

    async runGraph() {
      let xml = this.generateXml();
      let file = new File([xml], this.taskInfo.taskName + ".xml", {
        type: "text/xml",
      });
      let formData = new FormData();
      formData.append("file", file);
      formData.append("taskName", this.taskInfo.taskName);
      let data = await post(`/GeoProblemSolving/r/integrateTasks`, formData);
    },

    currentEventWithFile(val) {
      let dataItem = {
        name: val.name,
        value: val.value,
        eventId: val.eventId,
        isDirect: val.isDirect,
        reference: "",
      };

      this.graph.getModel().beginUpdate();

      try {
        Object.values(this.graph.getModel().cells).forEach((cell) => {
          if (cell.id == val.id) {
            cell.value = val.value;
          }
        });
      } finally {
        this.graph.getModel().endUpdate();
      }
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

      // console.log(this.graph.getModel().cells);
      let links = Object.values(this.graph.getModel().cells).filter((cell) =>
        cell.hasOwnProperty("edge")
      );
      this.linkEdgeList = links;

      this.toolListInGraph = toolListInGraph;
      this.dataOutputInGraph = dataOutputInGraph;
      this.dataInputInGraph = dataInputInGraph;
      this.dataLinkInGraph = dataLinkInGraph;
    },

    async getProcessDoc() {
      let nodeList = [];
      let modelItemList = [];
      let dataItemList = [];

      let toolListInGraph = this.toolListInGraph;
      let dataOutputInGraph = this.dataOutputInGraph;
      let dataInputInGraph = this.dataInputInGraph;
      let dataLinkInGraph = this.dataLinkInGraph;
      let linkEdgeList = this.linkEdgeList;

      await Promise.all(
        this.toolListInGraph.map(async (tool) => {
          //GET NODE
          let node = {};
          node.name = tool.name;
          node.description = tool.description; //model description
          node.type = "simulationDeduction"; //dataProcessing/simulationDeduction/visualization/resultAnalysis
          node.step = tool.id;
          node.ref = tool.doi; //model doi//除了wzh integratedtask之外 其他均用doi作为标识符
          nodeList.push(node);

          //GET MODEL INFOMATION
          let data = await get(
            `/GeoProblemSolving/modelTask/ModelBehaviorOrdinary/${tool.doi}`
          );
          tool.md5 = data.md5; //integrate task

          //get modelItemList

          let modelItem = {
            name: data.name,
            descriprion: data.description,
            modelRef: tool.doi,
            nodeRef: tool.id,
            stateList: [],
          };

          //GET ALL RELATED EVENT
          let eventList = [];
          let dataItemList = [];
          linkEdgeList.forEach((link) => {
            if (
              link.target.id == tool.id &&
              link.source.style.includes("dataInputType")
            ) {
              let event = {
                name: link.source.name,
                dataRef: link.source.value,
                stateName: link.source.stateName,
                type: "input",
                datasetItem: link.target.datasetItem,
              };
              // if (link.source.hasOwnProperty("datasetItem")) {
              //   event.datasetItem = link.source.datasetItem;
              // }

              eventList.push(event);

              if (link.source.value == undefined) {
                return;
              }
              // let dataItem={
              //   name=
              // }
            }
            if (
              link.source.id == tool.id &&
              link.target.style.includes("dataOutputType")
            ) {
              let event = {
                name: link.target.name,
                dataRef: link.target.value,
                stateName: link.target.stateName,
                type: "output",
                datasetItem: link.target.datasetItem,
              };
              eventList.push(event);
            }
          });

          //获得 这个model的所有state
          let stateInfoList =
            data.mdlJson.ModelClass[0].Behavior[0].StateGroup[0].States[0]
              .State;
          let stateInfos = [];
          stateInfoList.forEach((state) => {
            let { Event, ...list } = state;
            list.Event = [];
            stateInfos.push(list);
          });

          //将已有的event塞入其中
          stateInfos.forEach((state) => {
            eventList.forEach((item) => {
              if (state.name == item.stateName) {
                state.Event.push(item);
              }
            });
          });
          modelItem.stateList = stateInfos;
          modelItemList.push(modelItem);
        })
      );

      console.log(dataInputInGraph);
      dataInputInGraph.forEach((input) => {
        if (input.fileId == "" || input.fileId == undefined) {
          return;
        }
        let dataItem = {
          name: input.fileName,
          value: input.value,
          id: input.fileId,
        };
        dataItemList.push(dataItem);
      });

      let method = {
        userId: this.userInfo.userId,
        pid: this.projectId,
        nodeList: nodeList,
        modelCollection: modelItemList,
        dataCollection: dataItemList,
      };
      console.log(method);
      let data = await post(`/GeoProblemSolving/r/methods`, method);
      console.log(data);
      //modelItem
    },

    generateXml() {
      let version = "1.0";
      let uid = this.generateGUID();
      let dataLinks = [];
      let xml = "";
      let name = this.taskInfo.taskName;

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
      // console.log(xml);
      return xml;
    },

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

    generateTaskModelInfo() {
      let addModelActions = [];
      //拼接集成模型中的models部分
      let tools = this.toolListInGraph;
      tools.forEach((tool) => {
        let addModelAction = {
          id: tool.id,
          modelOid: tool.doi,
          md5: tool.md5,
          name: tool.name,
          description: tool.description,
          outputData: [],
          inputData: [],
          step: tool.step,
          iterationNum: tool.iterationNum,
        };

        let inputList = this.dataInputInGraph.filter(
          (event) => event.md5 == tool.md5
        );
        let outputList = this.dataOutputInGraph.filter(
          (event) => event.md5 == tool.md5
        );

        inputList.forEach((item) => {
          let inputData = {
            eventId: item.eventId,
            data: item.data,
            eventName: item.name,
            optional: item.optional,
            stateName: item.stateName,
          };
          if (item.type == "url") {
            inputData.type = item.type;
            inputData.value = item.value;
            inputData.fileName = item.fileName; //文件名
            inputData.suffix = item.suffix; //文件类型
          } else if (item.type == "link") {
            let link = this.linkEdgeList.filter(
              (el) => el.target.eventId == item.eventId
            );
            inputData.link = item.link;
            inputData.linkEvent = item.link[0].target.eventId;
          }
          addModelAction.inputData.push();
        });

        outputList.forEach((item) => {
          addModelAction.inputData.push({
            eventId: item.eventId,
            data: item.data,
            eventName: item.name,
            optional: item.optional,
            stateName: item.stateName,
          });
        });
        addModelActions.push(addModelAction);
      });

      // console.log(addModelActions);
      return addModelActions;
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
.main {
  width: 100%;
  height: 100%;
  display: flex;
  position: relative;
  .mainContainerMask {
    // display: flex;
    position: relative;
    width: 100%;
    .taskInfoForm {
      margin: 100px 190px;
      width: 700px;
    }
    .taskInfoBtn {
      // margin: 80px 50px;
      text-align: center;
      .createTask {
        margin-left: 400px;
        width: 200px;
        float: left;
      }
      .or {
        margin-left: 8px;
        width: 10px;
        font-size: 18px;
        line-height: 40px;
        float: left;
      }
      .selectTask {
        width: 200px;
        line-height: 40px;
        float: left;
      }
      .newTaskBtnTip {
        clear: both;
        margin-top: 10px;
        font-style: italic;
        color: rgb(102, 102, 102);
        font-size: 14px;
      }
    }
    z-index: 999;
  }

  .integrateTaskContainer {
    position: relative;
    right: 0;
    width: 195px;
    padding: 0 5px;
    box-shadow: 0px 0px 5px rgb(207, 207, 207);
  }

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
      // z-index: 1;
    }

    .editCellContainer {
      position: absolute;
      width: 300px;
      padding: 0 5px;
      background-color: rgba(255, 255, 255, 0.5);
      z-index: auto;
      height: 90%;
      top: 60px;
      right: 0px;
    }

    .mainContainer {
      width: 100%;
      height: 100%;
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
}
</style>