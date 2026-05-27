<template>
  <div class="one"></div>
</template>

<script>
  export default {
    name: "line"
  }
</script>

<style scoped>
  .one {
    margin-left: -1.5vh;
    margin-top: 2vh;
    width: 100%;
    height: 2vh;
    background: linear-gradient(
      to right,
      transparent 0%,
      transparent 50%,
      #ccc 50%,
      #ccc 100%
    );
    background-size: 30px 1px;
    background-repeat: repeat-x;
  }
</style>