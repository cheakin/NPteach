<template>
  <el-tree
    :data="data"
    :props="defaultProps"
    @node-click="handleNodeClick"
  ></el-tree>
</template>

<script>
export default {
  name: "category",
  components: {},
  directives: {},
  data() {
    return {
      data: [],
      defaultProps: {
        children: "children",
        label: "label"
      }
    };
  },
  mounted() {},
  methods: {
    handleNodeClick(data) {
      console.log(data);
    },
    getMenus() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get"
      }).then(data => {
        console.log(data);
      });
    }
  },
  created() {
    this.getMenus();
  }
};
</script>

<style scoped></style>
