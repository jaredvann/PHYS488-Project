import java.io.*; 
import java.util.Random;

public class MuonFactory
{
    static BufferedReader keyboard = new BufferedReader (new InputStreamReader(System.in)) ;
    static PrintWriter screen = new PrintWriter( System.out, true);
    static Random value = new Random();
    static int underflow;

    public MuonFactory(){
    }

    public static double muonAngle()
    {
        double nextangle;
        nextangle = value.nextDouble()*Math.PI*2;
        return nextangle;
    }

    public static double muonMomentum(){
        double nextP;
        double MaxP = 2000;
        double MinP = 200;
        nextP =value.nextDouble()*(MaxP-MinP)+MinP;
        return nextP;
        }

    }
