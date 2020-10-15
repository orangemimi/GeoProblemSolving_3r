<template>
  <div>
    <div>
      <div class="modalTitle2">Data Uploaded</div>
      <div style="line-height: 45px; margin-left: 10px">
        <!-- <el-checkbox
          v-model="checkDirectResourceAll"
          @change="handleCheckDirectResourceAll"
          >Select All</el-checkbox
        > -->
      </div>
      <div>
        <el-table
          :data="filterCheckedToDirect"
          border
          max-height="240"
          style="width: 100%"
        >
          <el-table-column prop="name" label="Instance name" width="200">
          </el-table-column>
          <el-table-column label="Selected for next" width="150">
            <template slot-scope="scope">
              <el-checkbox
                :checked="scope.row.checkedToNextStep"
                @change="changeDirectChecked($event, scope.$index)"
              ></el-checkbox>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div>
      <div class="modalTitle2">Data from the last step</div>
      <div>
        <el-table
          :data="filterCheckedeToInherit"
          border
          max-height="240"
          style="width: 100%"
        >
          <el-table-column
            prop="stepBindName"
            label="Previous Step"
            width="200"
          >
          </el-table-column>

          <el-table-column prop="name" label="Instance name" width="200">
          </el-table-column>
          <el-table-column label="Selected for next" width="150">
            <template slot-scope="scope">
              <el-checkbox
                :checked="scope.row.checkedToNextStep"
                @change="changeChecked($event, scope.$index)"
              ></el-checkbox>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { get, del, post, put, patch } from "../../../axios";
export default {
  props: {
    lastSteps: {
      type: Array,
    },
      stepInformation: {
      type: Object,
    },
  },

  watch: {
    lastSteps: {
      handler(val) {
        this.lastStepList = val;
      },
      deep: true,
    },
    stepInformation: {
      handler(val) {
        this.stepInfo = val;
      },
      deep: true,
    },
  },

  data() {
    return {
      projectId: this.$route.params.projectId,
      userInfo: this.$store.getters.userInfo,
      stepInfo: this.stepInformation,

      checkDirectResourceAll: false,
      checkInheritResourceAll: false,
      inheritResourceList: [],
      dataItemList: [],

      //查看上一步
      lastStepList: this.lastSteps,
      initDirectResource: [],
      initResource: [],
      
    };
  },
  computed: {
    filterCheckedeToInherit: {
      get() {
        if (this.initResource != undefined) {
          let dataItemList = this.initResource;
          let last = this.lastStepList;
          dataItemList.forEach((item) => {
            item.checkedToNextStep = false;
            if (item.stepInherit != null) {
              if (item.stepInherit.some((e) => e == this.stepInfo.stepId)) {
                item.checkedToNextStep = true;
              } else {
                item.checkedToNextStep = false;
              }
            } else {
              item.checkedToNextStep = false;
            }
          });
          //   console.log(dataItemList);
          return dataItemList.filter((item) => {
            return (
              item.stepBindId != null &&
              last.some((e) => e.stepID == item.stepBindId)
            );
          });
        }
      },
      set(val) {},
    },

    filterCheckedToDirect() {
      if (this.initDirectResource != undefined) {
        let dataItemList = this.initDirectResource;
        dataItemList.forEach((item) => {
          item.checkedToNextStep = false;
          if (item.stepInherit != null) {
            if (item.stepInherit.some((e) => e == this.stepInfo.stepId)) {
              item.checkedToNextStep = true;
            } else {
              item.checkedToNextStep = false;
            }
          } else {
            item.checkedToNextStep = false;
          }
        });
        // console.log(dataItemList);
        return dataItemList;
      }
    },
  },
  methods: {
    async getDataItem() {
      let dataItem = await get(
        `/GeoProblemSolving/r/dataItems/${this.projectId}`
      );
      this.$set(this, "initResource", dataItem);
      dataItem = dataItem.filter((item) => {
        return item.isDirect == true;
      });
      this.$set(this, "initDirectResource", dataItem);
    },

    // handleCheckDirectResourceAll(val) {
    //   this.checkDirectResourceAll = val;
    //   this.initDirectResource.forEach((item) => {
    //     item.checkedToNextStep = this.checkDirectResourceAll; //只改变数据的状态
    //   });
    // },
    // handleCheckInheritResourceAll(val) {
    //   this.checkInheritResourceAll = val;
    //   this.inheritResourceList.forEach((item) => {
    //     item.checkedToNextStep = this.checkInheritResourceAll; //只改变数据的状态
    //   });
    // },

    async changeChecked(val, index) {
      this.$set(this.filterCheckedeToInherit[index], "checkedToNextStep", val);
      await this.updateDataItem(val, this.filterCheckedeToInherit[index]);
      this.$forceUpdate();
    },

    async changeDirectChecked(val, index) {
      this.$set(this.filterCheckedToDirect[index], "checkedToNextStep", val);
      await this.updateDataItem(val, this.filterCheckedToDirect[index]);
      this.$forceUpdate();
    },

    async updateDataItem(val, data) {
      console.log(val, data);
      if (val) {
        //如果被选中了inherit
        if (data.stepInherit != null) {
          //如果stepInherit已经有了内容，且其中的stepList没有一个是当前step
          if (data.stepInherit.every((e) => e != this.stepInfo.stepId)) {
            data.stepInherit.push(this.stepInfo.stepId);
            await this.update(data.id, data);
          }
          //如果stepInherit已有当前step,则不操作
        } else {
          //如果stepInherit=null，直接插入
          data.stepInherit = [];
          data.stepInherit.push(this.stepInfo.stepId);
          await this.update(data.id, data);
        }
      } else {
        //如果没被选中
        if (data.stepInherit != null) {
          if (data.stepInherit.some((e) => e == this.stepInfo.stepId)) {
            data.stepInherit.splice(
              data.stepInherit.findIndex((id) => id === this.stepInfo.stepId),
              1
            );
            await this.update(data.id, data);
          }
        }
      }
    },
    async update(id, resource) {
      let data = await patch(`/GeoProblemSolving/r/dataItems/${id}`, resource);
    },
  },
  mounted() {
    this.getDataItem();
  },
};
</script>

<style>
</style>