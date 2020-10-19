<template>
  <div class>
    <el-row>
      <mx-graph :sendXml="sendFlowChartFromXml"></mx-graph>
    </el-row>
  </div>
</template>

<script>
import mxGraph from "@/components/utils/mxGraph/mxGraph";
import { get, del, post, patch } from "../../../axios";

export default {
  components: { mxGraph },

  props: {
    createMapInstances: {
      type: Array,
    },
    dataItems: {
      type: Array,
    },
    initXml: {
      type: String,
    },
  },

  watch: {
    dataItems: {
      handler(val) {
        this.dataItemList = val;
      },
      deep: true,
    },
    initXml: {
      handler(val) {
        this.sendXml = val;
      },
      deep: true,
    },

    createMapInstances: {
      handler(val) {
        this.checkedInstances = val;
        // console.log("checkedINs", val);
        this.initCreate();
      },
      deep: true,
    },
  },

  computed: {
    filterDirectDataResource() {
      return this.dataItemList.filter((data) => data.isDirect == true);
    },
    filterIndirectDataResource() {
      return this.dataItemList.filter((data) => data.isDirect == false);
    },
    sendFlowChartFromXml() {
      return `<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>${this.sendXml}</root></mxGraphModel>`;
    },
  },

  data() {
    return {
      checkedInstances: this.createMapInstances,
      instances: [],
      dataItemList: this.dataItems,
      contentHeight: 0,

      sendXml: "",

      mxNodes: [],

      dataNodes: [],
      dataNodesIntermedia: [],

      nextStartIndex: 0,
      // updateXml: false,
      stepInfo: this.$route.params,
      createPositionIndex: 1,
      lastNode: {},
    };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      // console.log(this.$route.params);
      this.contentHeight = window.innerHeight - 270;
    },
    async initCreate() {
      this.dataNodes = [];
      this.dataNodesIntermedia = [];

      // let directDataResource = [...this.filterDirectDataResource];
      // let inDirectDataResource = [...this.filterIndirectDataResource];
      // console.log(this.filterIndirectDataResource);
      let directDataResource = JSON.parse(JSON.stringify(this.dataItemList));
      // let inDirectDataResource = JSON.parse(
      //   JSON.stringify(this.filterIndirectDataResource)
      // );
      this.instances = JSON.parse(JSON.stringify(this.checkedInstances));
      // let checkedInstances = [...this.checkedInstances];
      this.getNodeLinkInstance(directDataResource, this.instances);
      // this.getNodeLinkInstance(inDirectDataResource, checkedInstances);
      this.mxNodes = [];
      console.log(this.dataNodes, this.instances);
      this.getNodes(this.dataNodes);
      this.createXml();

      this.$emit("emitxml", this.sendXml);
      // await this.createFlowChart();
    },

    getNodeLinkInstance(dataResource, checkedInstances) {
      // console.log(checkedInstances);
      checkedInstances.forEach((instance) => {
        dataResource.forEach((data) => {
          if (
            data.hasOwnProperty("toModelInstanceList") &&
            data.toModelInstanceList != null
          ) {
            let toModelInstanceList = data.toModelInstanceList;
            toModelInstanceList.forEach((id) => {
              if (instance.id == id) {
                data.hasOwnProperty("to") ? data["to"] : (data["to"] = []);
                instance.hasOwnProperty("from")
                  ? instance["from"]
                  : (instance["from"] = []);
                data["to"].push(instance);
                instance["from"].push(data);
                // if (!data.hasOwnProperty("from")) {
                this.dataNodes.includes(data) ? "" : this.dataNodes.push(data);
                // }
              }
            });
          }

          if (
            data.hasOwnProperty("fromModelInstance") &&
            data.fromModelInstance != null
          ) {
            let fromModelInstance = data.fromModelInstance;
            // console.log(fromModelInstance);
            if (instance.id === fromModelInstance) {
              data.hasOwnProperty("from") ? data["from"] : (data["from"] = []);
              instance.hasOwnProperty("to")
                ? instance["to"]
                : (instance["to"] = []);
              data["from"].push(instance);
              instance["to"].push(data);
              this.dataNodes.includes(data) ? "" : this.dataNodes.push(data);
              // console.log(instance);
            }
          }
        });
      });
    },

    getNodes(dataNodes) {
      this.nextStartIndex = 3;
      dataNodes.forEach((node, index) => {
        if (!this.mxNodes.some((node2) => node.id === node2.id)) {
          node.mxIndex = this.nextStartIndex;
          node.vertex = "1";
          this.mxNodes.push(node);

          if (node.hasOwnProperty("to") && node.to != []) {
            this.nextStartIndex += node.to.length * 2;
            this.getNextInstance(node.to, node.mxIndex);
          } else {
            this.nextStartIndex += 2;
            console.log("mxNodes", this.mxNodes);
          }
        }
      });
    },

    getNextInstance(instances, sourceNodeIndex) {
      instances.forEach((nextInstance, nextIndex) => {
        //查看mxNodes中已有的instance 即多输入文件 一个模型的情况
        if (this.mxNodes.some((node) => nextInstance.id === node.id)) {
          let duplicateNode = this.mxNodes.filter(
            (node) => nextInstance.id == node.id
          );
          console.log(duplicateNode);
          let lineNode = {
            mxIndex: this.nextStartIndex - 1,
            source: sourceNodeIndex,
            target: duplicateNode[0].mxIndex,
            edge: "1",
          };
          this.mxNodes.push(lineNode);
        } else {
          nextInstance.mxIndex = this.nextStartIndex; //mxTargetIndex=mxIndex
          nextInstance.mxSourceIndex = sourceNodeIndex;
          nextInstance.vertex = "1";
          this.mxNodes.push(nextInstance);

          let lineNode = {
            mxIndex: this.nextStartIndex - 1,
            source: sourceNodeIndex,
            target: this.nextStartIndex,
            edge: "1",
          };
          this.mxNodes.push(lineNode);
          if (
            nextInstance.hasOwnProperty("to") &&
            nextInstance.to != [] &&
            nextInstance.to != undefined
          ) {
            // for (let i = 0; i < nextInstance.to.length; i++) {
            this.nextStartIndex += nextInstance.to.length * 2;
            this.getNextInstance(nextInstance.to, nextInstance.mxIndex);
            // }
          } else {
            this.nextStartIndex += 2;
          }
        }
      });
    },

    createXml() {
      let nodes = this.mxNodes;
      this.createPositionIndex = 0;
      // console.log(this.mxNodes);
      let xml = "";

      let dataNodeStyle =
        "fillColor=#f8f5ec;strokeColor=rgb(200, 200, 200);strokeWidth=1;shape=ellipse;align=center;imageAlign=center;imageVerticalAlign=top";
      let modelNodeStyle =
        "fillColor=transparent;strokeColor=#000000;strokeWidth=1;shape=rectangle;align=center;imageAlign=center;imageVerticalAlign=top";

      let edgeNodes = [];
      let modelNodes = [];
      let directNodes = [];
      let inDirectNodes = [];
      nodes.forEach((node) => {
        if (node.hasOwnProperty("edge")) {
          edgeNodes.push(node);
        } else if (!node.hasOwnProperty("isDirect")) {
          modelNodes.push(node);
        } else if (node.hasOwnProperty("isDirect")) {
          if (node.isDirect) {
            directNodes.push(node);
          } else {
            if (!node.hasOwnProperty("from")) {
              directNodes.push(node);
            } else {
              inDirectNodes.push(node);
            }
          }
        }
      });
      console.log(edgeNodes, modelNodes, directNodes, inDirectNodes);

      modelNodes.forEach((model, modelIndex) => {
        let pIndex = modelIndex * 3;
        xml += this.xmlVertex(
          model.mxIndex,
          model.name,
          modelNodeStyle,
          modelIndex * 160,
          (pIndex + 1) * 150
        );
        let directDataIndex = 0;
        let inDirectDataIndex = 0;
        directNodes.forEach((node, dataIndex) => {
          if (node.to.some((to) => to.id === model.id)) {
            xml += this.xmlVertex(
              node.mxIndex,
              node.name,
              dataNodeStyle,
              directDataIndex * 150,
              pIndex * 150
            );
            directDataIndex++;
          }
        });
        inDirectNodes.forEach((node, dataIndex) => {
          if (node.from.some((to) => to.id === model.id)) {
            xml += this.xmlVertex(
              node.mxIndex,
              node.name,
              dataNodeStyle,
              inDirectDataIndex * 150,
              (pIndex + 2) * 150
            );
            inDirectDataIndex++;
          }
        });
      });
      edgeNodes.forEach((node) => {
        xml += this.xmlEdge(node.mxIndex, node.source, node.target);
      });

      this.sendXml = xml;
    },

    xmlVertex(id, title, style, x, y) {
      let stepId = this.$route.params.stepId;
      let content = `<mxCell id="${stepId}+${id}" title="${title}" vertex="1" parent="1" style="${style}"><mxGeometry x="${x}" y="${y}" width="150" height="100" as="geometry" /></mxCell>`;
      return content;
    },

    xmlEdge(id, source, target) {
      let stepId = this.$route.params.stepId;
      let content = `<mxCell id="${stepId}+${id}"  source="${stepId}+${source}" target="${stepId}+${target}" edge="1" parent="1"><mxGeometry relative="1" as="geometry"/></mxCell>`;
      return content;
    },
  },
  created() {
    this.initSize();
  },
  // mounted() {
  //   this.getMap();
  // },
};
</script>
<style lang='scss' scoped>
.mapTitle {
  //   line-height: 35px;
  font-size: 35px;
  font-weight: 600;
  margin: 30px 0 10px 0;
}
</style>