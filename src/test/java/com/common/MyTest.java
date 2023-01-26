package com.common;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyTest {
    Integer i = new Integer(1000);

    @Test
    public void test1() {
        String regex = "^\\w+(\\w|[.]\\w+)+@\\w+([.]\\w+){1,3}";

        System.out.println("1873138022@qq.com".matches(regex));
    }

    @Test
    public void test2() {
        //单列集合
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        list.stream().distinct();
        list.stream().forEach(s -> System.out.println(s));
        System.out.println("===============================");
        //双列集合,需要对双列集合使用 keySet 或 entrySet 方法转为单列集合再做处理
        HashMap<String, Integer> hm = new HashMap();
        hm.put("aaa", 111);
        hm.put("bbb", 222);
        hm.put("ccc", 333);
        hm.put("ddd", 444);

        Set<String> keySet = hm.keySet();
        keySet.stream().forEach(s -> System.out.println(s));
        System.out.println("++++++++++++++++++++++++++++++++++");
        Set<Map.Entry<String, Integer>> entrySet = hm.entrySet();
        entrySet.stream().forEach(s -> System.out.println(s));
        System.out.println("----------------------------------------");
        //数组
        //1.基本数据类型，只能使用 Arrays 工具类中的 stream() 方法,
        // 因为对于基本数据类型，Stream.of() 方法会将整个数组对象当作一个数据，不会拆开
        int[] arr1 = {1, 2, 3, 4, 5};
        Arrays.stream(arr1).forEach(arr -> System.out.println(arr));
        System.out.println("===========================================");
        //2.引用数据类型,可以使用 Arrays 中的 stream() 方法以及 Stream 中的 of() 方法
        String[] arr2 = {"a", "b", "c", "d"};
        Arrays.stream(arr2).forEach(arr -> System.out.println(arr));
        System.out.println("===================================");
        Stream.of(arr2).forEach(arr -> System.out.println(arr));
        System.out.println("====================================");

        //零散数据,可使用 Stream.of() 静态方法
        Stream.of(1, 2, 3, 4, 5, true, "s", "1.2", 100000L, 'a').forEach(a -> System.out.println(a));

    }

    @Test
    public void test3() {

        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, "zsf-20", "zzr-18", "lyf-18", "lwt-100");
        list.stream().map(s -> Integer.parseInt(s.split("-")[1])).forEach(s -> System.out.println(s));
    }

    @Test
    public void getOdd() {
        ArrayList<Integer> list = new ArrayList<>();
        Collections.addAll(list, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> collect = list.stream().filter(num -> num % 2 != 0).collect(Collectors.toList());
        collect.forEach(item -> System.out.println(item));
    }

    @Test
    public void getObjFromString() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, "zhangsan,24", "lisi,25", "wangwu,21");
        Map<String, Integer> collect = list.stream()
                .filter(s -> Integer.parseInt(s.split(",")[1]) >= 24)
                .collect(Collectors.toMap(s -> s.split(",")[0], s -> Integer.parseInt(s.split(",")[1])));
        System.out.println(collect);
    }

    @Test
    public void getTowList() {
        ArrayList<String> manList = new ArrayList<>();
        ArrayList<String> womanList = new ArrayList<>();
        Collections.addAll(manList, "张三,23", "张三丰,24", "李四,26", "王五,21", "张晓明,25", "王小明,23");
        Collections.addAll(womanList, "李晓红,20", "杨梦露,23", "迪丽热巴,21", "杨紫,24", "周芷若,26", "abc,32");
        List<String> newManList = manList.stream()
                .filter(s -> s.split(",")[0].length() == 3)
                .limit(2)
                .collect(Collectors.toList());
        List<String> newWomanList = womanList.stream()
                .filter(s -> s.split(",")[0].startsWith("杨"))
                .skip(1)
                .collect(Collectors.toList());
        List<String> collect = Stream.
                concat(newManList.stream(), newWomanList.stream()).
                collect(Collectors.toList());
        /*List<Actor> finalList = collect.stream().map(item -> {
            String name = item.split(",")[0];
            int age = Integer.parseInt(item.split(",")[1]);
            Actor actor = new Actor();
            actor.name = name;
            actor.age = age;
            return actor;
        }).collect(Collectors.toList());*/
        //在类中提供一个两个参数的构造方法即可使用 lambda 表达式
        List<Actor> finalList = collect.stream()
                .map(item -> new Actor(item.split(",")[0],
                        Integer.parseInt(item.split(",")[1])))
                .collect(Collectors.toList());
        finalList.forEach(item -> System.out.println(item));
    }

    @Test
    public void methodRef() {
        Integer[] arr = {1, 4, 23, 5, 6, 34, 3};

        Arrays.sort(arr, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        System.out.println(Arrays.toString(arr));
    }

    public static int sub(Integer n1, Integer n2) {
        return n2 - n1;
    }


    class Actor {
        private String name;
        private int age;

        public Actor() {
        }

        public Actor(String str){
            this.name = str.split(",")[0];
            this.age = Integer.parseInt(str.split(",")[1]);
        }

        public Actor(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Actor{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Actor actor = (Actor) o;
            return age == actor.age &&
                    Objects.equals(name, actor.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
    @Test
    public void funRef_static(){
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list,"1","2","3","4","5");
//        List<Integer> collect = list.stream().map(item -> Integer.parseInt(item)).collect(Collectors.toList());
//        collect.forEach(item -> System.out.println(item));

        list.stream().map(Integer::parseInt).forEach(i -> System.out.println(i));
    }

    @Test
    public void funRef_construct(){
        ArrayList<String> manList = new ArrayList<>();
        Collections.addAll(manList, "张三,23", "张三丰,24", "李四,26", "王五,21", "张晓明,25", "王小明,23");
        List<Actor> collect = manList.stream().map(Actor::new).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void FunctionalInterface(){
        Consumer<String> s1 = s -> System.out.println(s);
        Consumer<String> s2 = s -> System.out.println(s.length());
        s1.andThen(s2).accept("aaa");
    }

    public static void PrintNum(String str, Consumer<String> consumer){
        consumer.accept(str);
    }

    @Test
    public void funRef_className(){
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list,"aaa","bbb","ccc","ddd");
        list.stream().map(String::toUpperCase).forEach(s -> System.out.println(s));
        /*list.stream().map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s.toUpperCase();
            }
        }).forEach(s -> System.out.println(s));*/
    }

    @Test
    public void exercise1(){
        ArrayList<String> manList = new ArrayList<>();
        Collections.addAll(manList, "张三,23", "张三丰,24", "李四,26", "王五,21", "张晓明,25", "王小明,23");
        List<Actor> collect = manList.stream().map(Actor::new).collect(Collectors.toList());
      /*  for (Actor a: collect){
            System.out.println(a);
        }*/
        List<String> list = collect.stream()
                /*.map(item -> item.name + "-" + item.age)
                .map(item -> item.toString())*/
                .map(this::toString_)
                .collect(Collectors.toList());
        String[] strings = list.stream().toArray(String[]::new);
        for (String string : strings) {
            System.out.println(string);
        }
    /*    List<String> newNames = names.stream().map(item -> item + "-").collect(Collectors.toList());
        List<String> newAges = ages.stream().map(item -> item.toString()).collect(Collectors.toList());
        newNames.stream().map(item->item + ).forEach(i-> System.out.println(i));*/
    }

    @Test
    public void exTest(){
        int[] arr = {1,2,3,4};

        try{
            System.out.println(arr[4]);
            System.out.println("try中语句1");
            int a = 1/0;
            System.out.println("try中语句2");
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("catch中的语句");
        }catch (ArithmeticException e){
            System.out.println("catch中的语句2");
        }
        System.out.println("try外面的语句");
    }

    public String toString_(Actor actor){
        return actor.name + "-" + actor.age;
    }

    @Test
    public void myFileTest(){
        String str = "F:\\docs\\a.txt";
        File file = new File(str);
        try {
            if (!file.exists())
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(file.exists());
        System.out.println(file);
    }

    @Test
    void FileTest2(){
        File file = new File("E:\\");
        findAvi(file);
    }

    public static void findAvi(){
        //获取所有盘符
        File[] roots = File.listRoots();
        for (File root : roots) {
            //System.out.println(root);
            findAvi(root);
        }
    }

    public static void findAvi(File src){
        //1、找到当前盘符下的所有文件/文件夹
        File[] files = src.listFiles();
        if(files != null){
            for (File file : files) {
//                如果是文件，则判断文件名是否为 .avi
                if(file.isFile()){
                    if(file.getName().endsWith(".avi")){
                        System.out.println(file);
                    }
                }else {
//                    如果是文件夹，则递归调用
                    findAvi(file);
                }
            }
        }
    }
}