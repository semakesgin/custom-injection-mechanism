package tr.com.injection;

import tr.com.injection.annotations.Bean;
import tr.com.injection.annotations.Init;
import tr.com.injection.annotations.Inject;

    interface MyDao {
        void test();
    }

    @Bean
    class MyDaoImpl implements MyDao {
//        @Inject
//        private tr.com.innova.injection.beans.MyService myService;
        @Init
        public void init() {
            System.out.println("MyDao Created");
        }

        public void test() {
            System.out.println("MyDao: test");
        }
    }

    @Bean
    class MyService {

        @Inject
        private MyDao myDao;

        @Init
        public void init() {
            System.out.println("MyService Created");
        }

        public void test() {
            System.out.println("MyService: test");
            myDao.test();
        }
    }

    class InterfaceInjection {

        public static void main(String[] args) {
            ApplicationContext applicationContext = new ApplicationContextImpl();//Create application context
                    MyService myService = (MyService) applicationContext.getBeanByClass(MyService.class);
            myService.test();
            applicationContext.displayAllBeans();
        }
    }


