<style scoped>
h1 {
  text-align: center;
  margin-top: 2.5%;
}
.projectForm {
  width: 60%;
  margin-top: 0.5%;
  margin-left: 20%;
  margin-right: 20%;
}
.inline_style {
  display: flex;
}
.create {
  width: 20%;
  margin-right: 40%;
  margin-left: 40%;
}
#editor {
  /* position:fixed; */
  margin: 20px auto;
  bottom: 0;
  width: 80%;
  /* height: 200px; */
}
/* 上传图片 */
.demo-upload-list {
  display: inline-block;
  width: 60px;
  height: 60px;
  text-align: center;
  line-height: 60px;
  border: 1px solid transparent;
  border-radius: 4px;
  /* overflow-x: hidden; */
  /* overflow-y: scroll; */
  background: #fff;
  position: relative;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
  margin-right: 4px;
}
.demo-upload-list img {
  width: 100%;
  height: 100%;
}
.demo-upload-list-cover {
  display: none;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
}
.demo-upload-list:hover .demo-upload-list-cover {
  display: block;
}
.demo-upload-list-cover i {
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  margin: 0 2px;
}
.uploadAvatar {
  position: relative;
  width: 58px;
  height: 58px;
  top: 0;
  left: 0;
  outline: none;
  background-color: transparent;
  opacity: 0;
}
.uploadBox {
  display: inline-block;
  width: 58px;
  height: 58px;
  line-height: 58px;
  border-width: 0.75px;
  border-style: dashed;
  border-color: lightslategray;
}

/* 结束 */
</style>
<template>
  <div class="project form">
    <h1>New Project</h1>
    <div>
      <Form ref="formInline" :model="formInline" :rules="newProjectRule" class="projectForm">
        <!-- 选择类别 -->
        <FormItem prop="category" label="Category" :label-width="100">
          <RadioGroup v-model="formInline.category" style="width:80%">
            <Radio label="Investigational">Investigational project</Radio>
            <Radio label="Intercomparable">Intercomparable project</Radio>
            <Radio label="Reproducible">Reproducible project</Radio>
            <Radio label="Educational">Educational project</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem prop="title" label="Name" :label-width="100">
          <Input
            v-model="formInline.title"
            placeholder="Enter name of the project(less than 60 characters)..."
          />
        </FormItem>
        <FormItem prop="description" label="Description" :label-width="100">
          <Input
            v-model="formInline.description"
            placeholder="Enter a brief introduction (less than 180 characters)..."
          />
        </FormItem>
        <FormItem prop="privacy" label="Privacy" :label-width="100">
          <RadioGroup v-model="formInline.privacy" style="width:80%;margin-left:10px">
            <Radio
              label="Public"
              title="Other users can find the group and see who has membership."
            ></Radio>
            <Radio
              label="Discoverable"
              title="Other users can find this group, but membership information is hidden."
            ></Radio>
            <Radio label="Private" title="Other users can not find this group."></Radio>
          </RadioGroup>
        </FormItem>
        <FormItem prop="tag" label="Tag" :label-width="100">
          <Input
            v-model="inputTag"
            placeholder="Enter some tag to introduce the project"
            style="width: 400px"
            @keyup.enter.native="addTag(inputTag)"
          />
          <Button
            icon="ios-add"
            type="dashed"
            size="small"
            @click="addTag(inputTag)"
            style="margin-left:2.5%"
          >Add Tag</Button>
          <div>
            <Tag
              color="primary"
              v-for="(item,index) in this.formInline.tagList"
              :key="index"
              closable
              @on-close="deleteTag(index)"
            >{{item}}</Tag>
          </div>
          <div>
            <span>Or select:</span>
            <Tag style="cursor:pointer" @click.native="addTag('Terrestrial')">Terrestrial</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('Coastal')">Coastal</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('Marine')">Marine</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('Climate')">Climate</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('Ecological')">Ecological</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('Geological')">Geological</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('Human')">Human</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('GIS & RS')">GIS & RS</Tag>
            <Tag style="cursor:pointer" @click.native="addTag('General')">General</Tag>
          </div>
        </FormItem>
        <FormItem prop="image" label="Image" :label-width="100">
          <div class="inline_style">
            <div class="demo-upload-list" v-if="img!=''">
              <template>
                <img v-bind:src="img" />
                <div class="demo-upload-list-cover">
                  <Icon type="ios-eye-outline" @click.native="handleView()"></Icon>
                  <Icon type="ios-trash-outline" @click.native="handleRemove()"></Icon>
                </div>
              </template>
            </div>
            <div class="uploadBox">
              <Icon type="ios-camera" size="20" style="position:absolute;margin:18px;"></Icon>
              <input
                id="choosePicture"
                @change="uploadPhoto($event)"
                type="file"
                class="uploadAvatar"
                accept="image/*"
              />
            </div>
            <br />
            <Modal title="View Image" v-model="visible">
              <img :src="img" v-if="visible" style="width: 100%" />
            </Modal>
          </div>
        </FormItem>
        <FormItem prop="introduction" label="Introduction" :label-width="100">
          <div class="inline_style">
            <Input
              v-model="formInline.introduction"
              type="textarea"
              placeholder="Enter detailed introduction about this project..."
              style="height:100%;width:100%"
            />
          </div>
        </FormItem>
        <FormItem>
          <div class="inline_style">
            <Button
              type="success"
              @click="createProject('formInline')"
              class="create"
              :disabled="clickForbidden"
            >Create</Button>
          </div>
        </FormItem>
      </Form>
    </div>
  </div>
</template>
<script>
export default {
  beforeRouteEnter: (to, from, next) => {
    next(vm => {
      $.ajax({
        url: "/GeoProblemSolving/user/state",
        type: "POST",
        async: false,
        success: function(data) {
          if (!data) {
            vm.$store.commit("userLogout");
            vm.$router.push({ name: "Login" });
          }
        },
        error: function(err) {
          console.log("Get user state fail.");
        }
      });
    });
  },
  data() {
    return {
      // 控制按钮禁用的
      clickForbidden: false,
      formInline: {
        title: "",
        category: "",
        introduction: "",
        privacy: "Public",
        description: "",
        //tag列表
        tagList: []
      },
      // 权限的具体功能
      defaultPublic: {
        observe: {
          project_manager: null,
          subproject_manager: null,
          member: null,
          visitor: "All"
        },
        auto_join: {
          project_manager: null,
          subproject_manager: null,
          member: null,
          visitor: true
        },
        project_edit_info: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_invite_member: {
          project_manager: true,
          subproject_manager: null,
          member: true,
          visitor: null
        },
        project_remove_member: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_task_create: {
          project_manager: true,
          subproject_manager: null,
          member: true,
          visitor: null
        },
        project_task_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        project_resource_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        project_workspace_type_manage: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        subprojects_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        subproject_edit_info: {
          project_manager: true,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_invite_member: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        subproject_remove_member: {
          project_manager: true,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_task_create: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        subproject_task_manage: {
          project_manager: true,
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        subproject_resource_manage: {
          project_manager: "Yes, partly",
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        subproject_workspace_type_manage: {
          project_manager: true,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        activity_manage: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        workspace_resource: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        workspace_tool: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        workspace_edit: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        }
      },
      defaultPrivate: {
        observe: {
          project_manager: null,
          subproject_manager: null,
          member: null,
          visitor: "No"
        },
        auto_join: {
          project_manager: null,
          subproject_manager: null,
          member: null,
          visitor: null
        },
        project_edit_info: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_invite_member: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_remove_member: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_task_create: {
          project_manager: true,
          subproject_manager: null,
          member: true,
          visitor: null
        },
        project_task_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        project_resource_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        project_workspace_type_manage: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        subprojects_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        subproject_edit_info: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_invite_member: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_remove_member: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_task_create: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        subproject_task_manage: {
          project_manager: false,
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        subproject_resource_manage: {
          project_manager: "Yes, partly",
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        subproject_workspace_type_manage: {
          project_manager: true,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        activity_manage: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        workspace_resource: {
          project_manager: "Yes, partly",
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        workspace_tool: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        workspace_edit: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        }
      },
      defaultDiscoverable: {
        observe: {
          project_manager: null,
          subproject_manager: null,
          member: null,
          visitor: "No"
        },
        auto_join: {
          project_manager: null,
          subproject_manager: null,
          member: null,
          visitor: null
        },
        project_edit_info: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_invite_member: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_remove_member: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        project_task_create: {
          project_manager: true,
          subproject_manager: null,
          member: true,
          visitor: null
        },
        project_task_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        project_resource_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        project_workspace_type_manage: {
          project_manager: true,
          subproject_manager: null,
          member: false,
          visitor: null
        },
        subprojects_manage: {
          project_manager: true,
          subproject_manager: null,
          member: "Yes, partly",
          visitor: null
        },
        subproject_edit_info: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_invite_member: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_remove_member: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        subproject_task_create: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        subproject_task_manage: {
          project_manager: "Yes, partly",
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        subproject_resource_manage: {
          project_manager: "Yes, partly",
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        subproject_workspace_type_manage: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        activity_manage: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        },
        workspace_resource: {
          project_manager: true,
          subproject_manager: true,
          member: "Yes, partly",
          visitor: null
        },
        workspace_tool: {
          project_manager: true,
          subproject_manager: true,
          member: true,
          visitor: null
        },
        workspace_edit: {
          project_manager: false,
          subproject_manager: true,
          member: false,
          visitor: null
        }
      },
      newProjectRule: {
        title: [
          {
            required: true,
            message: "The title cannot be empty and no more than 60 characters",
            trigger: "blur",
            max: 60
          }
        ],
        category: [
          {
            required: true,
            message: "Please select category",
            trigger: "change"
          }
        ],
        privacy: [
          {
            required: true,
            message: "Please select privacy",
            trigger: "change"
          }
        ],
        introduction: [
          {
            required: false,
            message: "give a detailed introduction about this project",
            trigger: "blur"
          }
        ],
        description: [
          {
            required: true,
            message:
              "The description cannot be empty and no more than 360 characters",
            trigger: "blur",
            max: 360
          }
        ]
      },
      //用来存储输入的单个标签变量
      inputTag: "",
      visible: false,
      //表示图片
      img: "",
      pictureUrl: "",
      createProjectInfo: {}
    };
  },
  created() {
    // 加入判断，如果未登录自动跳转登录页面
    if (!this.$store.getters.userState) {
      this.$router.push({ name: "Login" });
    }

    Array.prototype.contains = function(obj) {
      var i = this.length;
      while (i--) {
        if (this[i] != undefined && this[i] === obj) {
          return true;
        }
      }
      return false;
    };
  },
  methods: {
    createProject(name) {
      let permission;
      if (this.formInline.privacy == "Public") {
        permission = this.defaultPublic;
      } else if (this.formInline.privacy == "Discoverable") {
        permission = this.defaultDiscoverable;
      } else if (this.formInline.privacy == "Private") {
        permission = this.defaultPrivate;
      }

      this.$refs[name].validate(valid => {
        if (valid) {
          this.clickForbidden = true;
          let createProjectForm = {};
          createProjectForm["category"] = this.formInline.category;
          createProjectForm["title"] = this.formInline.title;
          createProjectForm["tag"] = this.formInline.tagList.toString();
          createProjectForm["privacy"] = this.formInline.privacy;
          createProjectForm["picture"] = this.pictureUrl;
          createProjectForm["introduction"] = this.formInline.introduction;
          createProjectForm["description"] = this.formInline.description;
          createProjectForm["managerId"] = this.$store.getters.userId;
          createProjectForm["type"] = "";
          createProjectForm["stepId"] = "";
          createProjectForm["permissionManager"] = permission;

          this.axios
            .post("/GeoProblemSolving/project/create", createProjectForm)
            .then(res => {
              if (res.data == "Offline") {
                this.$store.commit("userLogout");
                this.$router.push({ name: "Login" });
              } else if (res.data === "Fail") {
                this.$Message.error("Create project fail.");
              } else {
                this.createProjectInfo = res.data;
                this.addHistoryEvent(this.createProjectInfo.projectId);
              }
            })
            .catch(err => {
              console.log(err);
            });
        } else {
        }
      });
    },
    //创建历史纪录的函数
    addHistoryEvent(scopeId) {
      let form = {};
      let description =
        this.$store.getters.userName +
        " created a " +
        this.formInline.category +
        " project named " +
        this.formInline.title;
      form["description"] = description;
      form["scopeId"] = scopeId;
      form["eventType"] = "project";
      form["userId"] = this.$store.getters.userId;
      this.axios
        .post("/GeoProblemSolving/history/save", form)
        .then(res => {
          if (res.data === "Success") {
            this.$store.commit("setProjectInfo", this.createProjectInfo);
            window.location.href =
              "/GeoProblemSolving/projectDetail/" +
              this.createProjectInfo.projectId;
          } else {
            confirm("Created project fail.");
          }
        })
        .catch(err => {
          console.log(err.data);
        });
    },
    addTag(tag) {
      if (tag != "" && !this.formInline.tagList.contains(tag)) {
        this.formInline.tagList.push(tag);
        this.inputTag = "";
      }
    },
    deleteTag(index) {
      this.formInline.tagList.splice(index, 1);
    },
    uploadPhoto(e) {
      // 利用fileReader对象获取file
      var file = e.target.files[0];
      var filesize = file.size;
      // 2,621,440   2M
      if (filesize > 2101440) {
        // 图片大于2MB
        this.$Message.error("size > 2MB");
      } else {
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = e => {
          // 读取到的图片base64 数据编码 将此编码字符串传给后台即可
          let formData = new FormData();
          formData.append("picture", file);
          this.axios
            .post("/GeoProblemSolving/project/picture", formData)
            .then(res => {
              if (res.data != "Fail") {
                this.pictureUrl = res.data;
                this.img = e.target.result;
                $("#choosePicture").val("");
              } else {
                this.$Message.error("upload picture Fail!");
              }
            })
            .catch();
        };
      }
    },
    handleView() {
      this.visible = true;
    },
    handleRemove() {
      this.img = "";
      this.pictureUrl = "";
    }
  }
};
</script>
