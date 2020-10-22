<template>
  <div class="process_data_title">
    Data Resources
    <!-- <div>
      <vue-scroll style="height: 200px">
        <div
          v-for="(data, index) in selectedResources.dataItemList"
          :key="index"
        >
          <div class="data_info">
            <div class="data_info_name">{{ data.name }}</div>
            <div class="data_info_hover">
              <i
                class="el-icon-download"
                @click="downloadDataResource(data)"
              ></i>
            </div>
          </div>
        </div>
      </vue-scroll>
    </div> -->
  </div>
</template>

<script>
export default {
  methods: {
    downloadDataResource(data) {
      window.open(data.url);
    },
  },
};
</script>

<style>
</style>