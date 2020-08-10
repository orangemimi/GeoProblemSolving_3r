import mxgraph from './index';

const {
  mxGraph,
  mxCellEditor,
  mxGraphHandler,
  mxEvent,
} = mxgraph;

export class Graph extends mxgraph {
    constructor(container) {
      super(container);
      this._init();
    }
  
    _init() {
      this._setDefaultConfig();
      // this._configConstituent();
      // this._putVertexStyle();
      // this._setDefaultEdgeStyle();
      // this._setAnchors();
      // this._configCustomEvent();
      // this._configCoder();
    }
  
    _setDefaultConfig() {
      this.setConnectable(true);
      mxEvent.disableContextMenu(this.container);
  
      // 固定节点大小
      this.setCellsResizable(false);
  
      // 编辑时按回车键不换行，而是完成输入
      this.setEnterStopsCellEditing(false);
      // 编辑时按 escape 后完成输入
      mxCellEditor.prototype.escapeCancelsEditing = false;
      // 失焦时完成输入
      mxCellEditor.prototype.blurEnabled = true;
  
      // 禁止节点折叠
      this.foldingEnabled = false;
      // 文本包裹效果必须开启此配置
      this.setHtmlLabels(true);
  
      // 拖拽过程对齐线
      mxGraphHandler.prototype.guidesEnabled = true;
  
      // 禁止游离线条
      this.setDisconnectOnMove(false);
      this.setAllowDanglingEdges(false);
      mxGraph.prototype.isCellMovable = cell => !cell.edge;
  
      // 禁止调整线条弯曲度
      this.setCellsBendable(true);
  
      // 禁止从将label从线条上拖离
      mxGraph.prototype.edgeLabelsMovable = false;
    }
  }