<template>
  <div class>
    <div>
      <Card class="cardContent">
        <div class="cardContent">
          <div>
            <div class="info infoName">Title:</div>
            <div class="info infoDetail">{{scopeInfo.title}}</div>
             <div style="clear: both;"></div>
          </div>

          <div>
            <div class="info infoName">Description:</div>
            <div class="info infoDetail">{{scopeInfo.description}}</div>
             <div style="clear: both;"></div>
          </div>

          <div>
            <div class="info infoName">Create time:</div>
            <div class="info infoDetail">{{scopeInfo.createTime}}</div>
             <div style="clear: both;"></div>
          </div>

          <div>
            <div class="info infoName">Privacy:</div>
            <div class="info infoDetail">{{scopeInfo.privacy}}</div>
             <div style="clear: both;"></div>
          </div>

          <div>
            <div class="info infoName">Manager name:</div>
            <div class="info infoDetail">{{scopeInfo.managerName}}</div>
             <div style="clear: both;"></div>
          </div>



        </div>
      </Card>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    projectInfo: {
      type: Object,
    },
  },
  components: {},
  watch: {
    projectInfo: {
      handler(val) {
        this.scopeInfo = val;
      },
      deep: true,
    },
  },
  data() {
    return { scopeInfo: this.projectInfo };
  },
  methods: {},
  created() {},
  mounted() {},
};
</script>
<style lang='scss' scoped>
.info {
  float: left;
}
.infoName{
    font-size: 16px;
    font-weight: 600;
}
.infoDetail{
    font-size: 16px;
    margin: 4px 0 0 6px;    
    line-height:  16px;
}
.cardContent {
  height: 90%;
  margin: 10px 0;
}
</style>