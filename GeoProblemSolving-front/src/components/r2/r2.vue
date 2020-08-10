<template>
  <div>
    <div class="content">
      <menu-left class="lineStyle"></menu-left>
      <div class="lineStyle" :style="{width:contentWidth+'px'}">
        <flow-chart :projectInfo="projectInfo" :userRole="userRole"></flow-chart>
      </div>
      <div class="lineStyle cardRight" :style="{height:contentHeight+'px'}">
      <info-right :projectInfo="projectInfo"></info-right>
      </div>
      <div style="clear: both;"></div>
    </div>
  </div>
</template>

<script>
import menuLeft from "./components/menuLeft";
import flowChart from "./components/flowChart";
import infoRight from "./components/infoRight";
import { post } from "../../axios";
import { get } from "../../axios";
export default {
  props: {},
  components: { menuLeft, flowChart, infoRight },
  data() {
    return {
      projectId: this.$route.params.projectId,
      userInfo: JSON.parse(sessionStorage.getItem("userInfo")),
      projectInfo: {},
      userRole: "Visitor",
      contentWidth: 0,
      contentHeight:0
    };
  },

  methods: {
    initSize() {
      this.contentWidth = window.innerWidth -450;
       this.contentHeight = window.innerHeight -150;
    },
    async getUserInfo() {
      if (this.userInfo == {} || this.userInfo == undefined) {
        let data = await post("/GeoProblemSolving/user/state");
        userInfo.userState = true;
        this.userInfo = userInfo;
      }
    },

    async getProjectInfo() {
      if (
        JSON.stringify(this.projectInfo) == "{}" ||
        this.projectInfo == undefined
      ) {
        let { data } = await this.axios.get(
          `/GeoProblemSolving/project/inquiry?key=projectId&value=${this.projectId}`
        );
        if (data == "Offline") {
          parent.location.href = "/GeoProblemSolving/login";
        }
        if (data != "None" && data != "Fail") {
          this.projectInfo = data[0];
          this.identifyUserRole();
          this.$store.commit("setProjectInfo", data[0]);
        } else {
          console.log(data);
        }
      } else {
        this.identifyUserRole();
      }
    },
    identifyUserRole() {
      //Manager|Member|Visitor
      var thisUserId = this.userInfo.userId;
      var projectInfo = this.projectInfo;
      var members = projectInfo.members;
      var managerId = projectInfo.managerId;
      if (managerId == thisUserId) {
        this.userRole = "Manager";
      } else {
        for (var i = 0; i < members.length; i++) {
          var userId = members[i].userId;
          if (userId == thisUserId) {
            this.userRole = "Member";
          }
        }
      }
    },

    changeProjectInfo(newProjectInfo) {
      this.projectInfo = newProjectInfo;
      parent.vm.projectInfo = newProjectInfo;
    },
  },
  created() {
    this.getUserInfo();
    this.getProjectInfo();
    this.initSize();
  },
  mounted() {},
};
</script>
<style lang='scss' scoped>
.content {
  min-width: 1200px;
}
.lineStyle {
  float: left;
}
.cardRight {
  width: 350px;
}
</style>