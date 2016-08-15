/**
 * Created by vanhunick on 15/08/16.
 */
public class Singleton {

    private static Singleton singleInstance;

    private Singleton(){};

    public static Singleton getSingleInstance(){
        if(singleInstance == null){
            singleInstance = new Singleton();
        }
        return singleInstance;
    }

    public String toString(){
        return "I am a singleton";
    }
}


class SingleTonUser {

    public void useSingleton(){
       Singleton i = Singleton.getSingleInstance();
        System.out.println(i);
    }

    public static void main(String[] args){
        SingleTonUser singleTonUser = new SingleTonUser();

        singleTonUser.useSingleton();
    }
}