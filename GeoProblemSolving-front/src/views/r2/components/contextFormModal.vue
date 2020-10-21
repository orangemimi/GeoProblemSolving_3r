<template>
  <div class>
    <el-form ref="contextForm" :model="contextForm" label-width="150px" @submit.native.prevent>
      <el-form-item label="Theme">
        <el-input
          v-if="tagInputVisible"
          v-model="themeTag"
          ref="tagInput"
          @keyup.enter.native="addTags"
          @blur="addTags"
          style="width:300px"
        ></el-input>
        <el-button v-else @click="showTagInput" type="text" size="small">+ New Tag</el-button>
        <div v-if="contextForm!=null">
          <el-tag
            :key="tagIndex"
            v-for="(tag,tagIndex) in contextForm.themes"
            closable
            :disable-transitions="false"
            @close="delTags(tag)"
            style="margin:0 2px"
          >{{tag}}</el-tag>
        </div>
      </el-form-item>
      <el-form-item label="Simulation Purpose">
        <el-input v-model="contextForm.purpose" placeholder="Please enter the content."></el-input>
      </el-form-item>
      <el-form-item label="Simulation Object">
        <el-input v-model="contextForm.object" placeholder="Please enter the content."></el-input>
      </el-form-item>
      <el-form-item label="Spatio-temporal scale">
        <el-input v-model="contextForm.scale" placeholder="Please enter the content."></el-input>
      </el-form-item>
      <el-form-item label="Discussion">
        <el-input v-model="contextForm.discussion" placeholder="Please enter the content."></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitContext">Submit</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { post, get, patch } from "../../../axios";
export default {
  props: {
    projectInfo: {
      type: Object,
    },
  },
  components: {},
  data() {
    return {
      contextForm: {
        themes: [],
        purpose: "",
        object: "",
        scale: "",
        discussion: "",
      },
      themeTag: "",
      tagInputVisible: false,
      projectInfomation: this.projectInfo,
      updateContext: true,
    };
  },
  methods: {
    async getContext() {
      let { data } = await get(
        `/GeoProblemSolving/r/contextDefinition/${this.projectInfomation.projectId}`
      );

      if (data == null) {
        this.updateContext = false;
      } else {
        this.updateContext = true;
        this.contextForm = data;
      }
    },
    submitContext() {
      this.$refs.contextForm.validate(async (valid) => {
        if (valid) {
          if (!this.updateContext) {
            let contextForm = this.contextForm;
            contextForm.userId = this.projectInfomation.userId;
            contextForm.pid = this.projectInfomation.projectId;
            let { data } = await post(
              `/GeoProblemSolving/r/contextDefinition`,
              contextForm
            );
            this.$message({
              message: "You have save the context successfully",
              type: "success",
            });
          } else {
            let { data } = await patch(
              `/GeoProblemSolving/r/contextDefinition/${this.projectInfomation.projectId}`,
              this.contextForm
            );
            this.$message({
              message: "You have update the context successfully",
              type: "success",
            });
          }
        }
      });
      this.$emit("closeForm", false);
    },
    showTagInput() {
      this.tagInputVisible = true;
      this.$nextTick((_) => {
        this.$refs.tagInput.$refs.input.focus();
      });
    },
    addTags() {
      let tag = this.themeTag;
      if (tag != "") {
        this.contextForm.themes.push(tag);
      }
      this.tagInputVisible = false;
      this.themeTag = "";
    },
    delTags(tag) {
      this.contextForm.themes.splice(this.contextForm.themes.indexOf(tag), 1);
    },
  },
  created() {
    this.getContext();
  },
  mounted() {},
};
</script>
<style lang='scss' scoped>
</style>