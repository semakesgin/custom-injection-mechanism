//package tr.com.innova.injection;
//
//import tr.com.innova.injection.annotations.Bean;
//import tr.com.innova.injection.annotations.Inject;
//
//@Bean
//class MyService1{
//
//    @Inject
//    private MyService2 myService2;
//    public void test(){
//        System.out.println("MyService1: test");
//    }
//}
//
//@Bean
//class MyService2{
//
//    @Inject
//    private MyService3 myService3;
//}
//
//@Bean
//class MyService3{
//
////    @Inject
////    private MyService1 myService1;
//}
//
//
//class CircularDependency{
//
//    public static void main(String[] args){
//        ApplicationContext applicationContext = new ApplicationContextImpl();//Create application context
//                MyService1 myService1 = (MyService1)applicationContext.getBeanByClass(MyService1.class);
//                myService1.test();
//        applicationContext.displayAllBeans();
//    }
//}
//
