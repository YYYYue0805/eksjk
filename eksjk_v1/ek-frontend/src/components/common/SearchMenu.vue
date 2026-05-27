<template>
  <el-menu :default-active="onRoutes" class="el-menu-demo" mode="horizontal" @select="handleSelect">
    <el-menu-item index="statisticPosi">数据统计</el-menu-item>
  </el-menu>
</template>

<script>
  export default {
    name: "SearchMenu",
    data() {
      return {
        activeIndex: 'statisticPosi',
      };
    },
    computed:{
      onRoutes(){
          if(this.$route.path.replace('/','')=='statistics'){
             return 'statistics';
          }else if(this.$route.path.replace('/','')=='statisticPosi'){
              return 'statisticPosi'
          }else{
              return this.$route.path.replace('/','');
          }
      },
    },
    methods: {
      handleSelect(key, keyPath) {
        this.$router.push({ name: keyPath})
      },
    }
  }
</script>

<style lang="less">
  @import "../../assets/less/variable.less";

  .search-user {
    display: flex;
    flex-wrap: wrap;

    .first {
      // width: 40%;
      margin-right: 1rem;
      margin-bottom: 1rem;
    }

    .title {
      font-size: @font-normal;
      margin-left: 10px;
    }
  }
</style>