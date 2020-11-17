<!--  -->
<template>
  <div>
    <el-row>
      <div v-for="(task, index) in taskList" :key="index">
        <el-col :span="22" :offset="1">
          <el-card @click.native="selectTask(task)">
            {{ task.taskName }}
          </el-card>
          <el-divider />
        </el-col>
      </div>
    </el-row>
  </div>
</template>

<script>
import { get, post, patch } from "@/axios";
export default {
  components: {},
  props: {
    currentTask: {
      type: Object,
    },
  },
  watch: {
    currentTask: {
      handler(val) {
        if (val != "") {
          this.newTask = val;
          this.taskList.push(val);
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
      taskList: [],
      newTask: this.addNewTask,
    };
  },

  methods: {
    async init() {
      await this.getIntegrateTasks();
    },
    async getIntegrateTasks() {
      let data = await get(
        `/GeoProblemSolving/r/integrateTasks/all/${this.projectId}`
      ); //获得该项目的所有tasks
      if (data != null) {
        this.taskList = data;
      }
    },
    selectTask(task) {
      console.log("task", task);
      this.$emit("selectTask", task);
    },
  },

  async mounted() {
    await this.init();
  },
};
</script>
<style lang='scss' scoped>
</style>