//package tr.com.innova.injection;
//
//import tr.com.innova.injection.annotations.Bean;
//import tr.com.innova.injection.annotations.Inject;
//
//@Bean
//class MyDao{
//    public void test(){
//        System.out.println("MyDao: test");
//    }
//}
//
//@Bean
//class MyService{
//
//    @Inject
//    private MyDao myDao;
//
//    public void test(){
//        System.out.println("MyService: test");
//        myDao.test();
//    }
//}
//
//class App{
//
//    public static void main(String[] args) {
//        ApplicationContext applicationContext = new ApplicationContextImpl();//Create application context
//                MyService myService = (MyService)applicationContext.getBeanByClass(MyService.class);
//        myService.test();
//        applicationContext.displayAllBeans();
//    }
//}
//
