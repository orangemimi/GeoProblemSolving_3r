<template>
  <div class>
    <el-menu class="menuItem" @select="handleSelect">
      <el-menu-item index="1">
        <i class="el-icon-menu menuIcon"></i>
      </el-menu-item>
      <el-menu-item index="2">
        <i class="el-icon-document menuIcon"></i>
      </el-menu-item>
      <el-menu-item index="3">
        <i class="el-icon-folder-opened menuIcon"></i>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  props: {},
  components: {},
  data() {
    return {};
  },
  methods: {
    handleSelect(key) {
      switch (key) {
        case "1":
          {
            this.$router.push({ name: "chatUtil" });
          }
          break;
        case "2":
          {
            this.$router.push({ name: "list" });
          }
          break;
        case "3":
          {
            this.$router.push({ name: "resources" });
          }
          break;
      }
    },
  },
  created() {},
  mounted() {},
};
</script>
<style lang='scss' scoped>
.menuItem {
  // padding-left: 16px;
  width: 80px;
  text-align: center;
  .menuIcon {
    font-size: 30px;
  }
}
</style>