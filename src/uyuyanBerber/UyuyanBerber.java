package uyuyanBerber;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
public class UyuyanBerber {
    public static void main(String a[])
    {
    	System.out.println("Dukkan acildi.");    	
    	int toplamBerberSayisi = 3, musteriID = 1;
    	Random r = new Random();   	
        double val = r.nextGaussian() * 100+ 500;
        int millisDelay = (int) Math.round(val);
        ExecutorService executor = Executors.newFixedThreadPool(10);    	
        Dukkan dukkan = new Dukkan(toplamBerberSayisi); 
        for(int i = 1; i <= toplamBerberSayisi; i++) 
        {
	        Berber berber = new Berber(dukkan, i); 
	        Thread berberthread = new Thread(berber);
	     	executor.execute(berberthread);
        }        
        for(int i=0;i<40;i++)
        {
          Musteri musteri = new Musteri(dukkan);
          Thread musterithread = new Thread(musteri);
          musteri.setName(musteriID++);
          executor.execute(musterithread);
          try
          {
          	Thread.sleep(millisDelay);
          }
          catch(InterruptedException iex)
          {
              iex.printStackTrace();
          }
        }       
        executor.shutdown();
    }
} 
class Berber implements Runnable
{
	Dukkan dukkan;
    int berber_id;
    public Berber(Dukkan dukkan,int id)
    {
        this.dukkan = dukkan;
        berber_id= id;
    }
    public void run()
    {
        System.out.println(berber_id+ " numarali berber dukkana girdi.");
        while(true)
        {
            dukkan.sacKesim(berber_id);
        }
    }
}
class Musteri implements Runnable
{	
    int name;
    Berber berber;
    Dukkan dukkan;
    int cnt;
    public Musteri(Dukkan dukkan)
    {
        this.dukkan = dukkan;
        cnt = dukkan.berber_cnt;
    } 
    public int getName() 
    {
        return name;
    }
    public void setName(int name) 
    {
        this.name = name;
    }    
    public void run()
    {
    	sacKesimeGit();
    }
    private synchronized void sacKesimeGit()
    {
        dukkan.musteriEkle(this);
    }
} 
class Dukkan
{
	Random r = new Random();
	Berber berber;
	int musait_berber= 1;
    int sandalye;
    List<Integer> listMusteri;
    UyuyanBerber sb;    
    int berber_cnt;
    public Dukkan(int berber_cnt)
    {
        sandalye = 20;
        listMusteri = new LinkedList<Integer>();
        this.berber_cnt = berber_cnt;
        musait_berber = berber_cnt;
    } 
    public void sacKesim(int id)
    {
        int musteri;        
        synchronized (listMusteri)
        { 
            while(listMusteri.size()==0)
            {
                System.out.println(id+ " numarali berber musterisini bekliyor ve sandalyesinde uyuyor.");
                try
                {                	
                	listMusteri.wait();
                }
                catch(InterruptedException iex)
                {
                    iex.printStackTrace();
                }
            }   
            musteri = (Integer)((LinkedList<?>)listMusteri).poll();
        }  
        double val = r.nextGaussian() * 500 + 2500;
        int millisDelay = (int) Math.round(val);
        try
        {   
        	musait_berber--;
            System.out.println(id +" numarali berber musteri"+musteri+" nin saclarini kesiyor. ");
            Thread.sleep(millisDelay);
            System.out.println(id+" numarali berber musteri"+musteri+" nin sac kesimini tamamladi.");
            System.out.println("Musteri"+musteri+" dukkandan ayrildi.");
            Thread.sleep(500);
            musait_berber++;
            if(listMusteri.size()>0)
            {
            	System.out.println(id+" numarali berber su an bos ve musteri odasindan yeni musteri cagiriyor.");
            }
        }
        catch(InterruptedException iex)
        {
            iex.printStackTrace();
        }        
    } 
    public void musteriEkle(Musteri musteri)
    {  	
       System.out.println("Musteri"+musteri.getName()+" dukkana giris yapti.");
       
       synchronized (listMusteri) 
       {  
    	   if(listMusteri.size() == sandalye)
           {
               System.out.println("Musteri"+musteri.getName()+" icin sandalye yok.");
               System.out.println("Musteri"+musteri.getName()+" dukkandan cikti.");
               return ;
           }  
    	   if(musait_berber==0)
           {
    		   System.out.println("Musteri"+ +musteri.getName()+" berber arýyor fakat tum berberler mesgul.");
    		   System.out.println("Bekleme odasinda musait sandalye sayisi: "+ (sandalye-listMusteri.size()));
    		   System.out.println("Musteri"+musteri.getName()+ " icin bekleme alaninda sandalye var.");
           }
    	   else {
    		   System.out.println("Musteri"+musteri.getName()+ " bosta berber ariyor.");
    	   }  
    	   ((LinkedList<Integer>)listMusteri).offer(musteri.getName());    	   
    	   if(listMusteri.size()>0)
           {   
    		   listMusteri.notify();
           } 
       }
    }    
}