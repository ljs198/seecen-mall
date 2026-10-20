<!-- 写 js 代码 -->
<script setup>
import { ref } from 'vue'  // 导入 组合式 API

// 1、定义数据
const count = ref(10) // 响应式数据

// 2、定义方法
const add = () => {
  count.value++
}


// 3、声明对象数据
const user = {
  username: "tom",
  age: 18,
  avatar: "https://img.touxiangkong.com/uploads/allimg/2023021310/vi4wwajcuru.jpg"
}


// 4、布尔类型的数据
const isShow = ref(true)

</script>

<!-- 写页面：模板语法 -->
<template>
  <div class="container">

    <h2>vue3基础语法快速入门</h2>


    <!-- 插值表达式：{{ }} -->
    <h3>count 的初始值为： {{ count }}</h3>

    <!-- @click 表示点击事件 -->
    <button @click="add">+</button>

    <hr>
    <!--  展示用户信息 -->
    <p>姓名：{{ user.username }}</p>
    <p>年龄：{{ user.age }}</p>

    <!-- v-bind:  用于 将数据绑定到 标签的属性上 -->
    <img v-bind:src="user.avatar" alt="">
    
    <hr>

    <!-- v-if: 判断条件是否成立,来确定是否显示元素 
    控制元素的 存在 或 不存在 
    -->
    <!-- 可以简写为 :   -->
    <button @click="isShow = !isShow">显/隐图片</button>
    <img :src="user.avatar" alt="" v-if="isShow">


    <hr>
    <!-- v-show: 判断条件是否成立,来确定是否显示元素 
    元素一直存在，只是改变它的  display 属性 
    -->
    <img :src="user.avatar" alt="" v-show="isShow">



  </div>
</template>

<!-- 写 css 样式  -->
<style scoped>

img{
  width: 100px;
  height: 100px;
  border-radius: 50px;
}

</style>