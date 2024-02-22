package thread;

public class Print {
    static int num =0;

    //多个线程打印1,2,3,4
    public static void main(String[] args) {

        for(int i=0;i<3;i++){
         new Thread(new P(i)).start();
        }
    }

    static class P implements Runnable{
        private int i;

        public P(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(i+"");
            synchronized (Print.class){
                while (num<=10){
                    if(num%3==i){
                        System.out.println(Thread.currentThread().getName()+"  "+num);
                        num++;
                        Print.class.notifyAll();
                    }else{
                        try {
                            Print.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
