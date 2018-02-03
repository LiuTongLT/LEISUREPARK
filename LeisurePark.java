import java.sql.*;
import java.util.ArrayList;
import java.util.*;
import java.time.LocalTime;
import java.util.Optional;

public class LeisurePark
{
    private String name;
    private int idCounter;
    private ArrayList<Attraction> attractions;
    private ArrayList<Visitor> registeredVisitors;

    public LeisurePark(String parkname)
    {
        name = parkname;
        attractions = new ArrayList<Attraction>();
        registeredVisitors = new ArrayList<Visitor>();
    }

    public int getNumberOfAttractions()
    {
        return attractions.size();
    }

    public int getNumberOfRegisteredVisitors()
    {
        return registeredVisitors.size();
    }

    public boolean registerVisitor(Visitor newVisitor)
    {
        Iterator<Visitor> it = registeredVisitors.iterator();
        boolean boo = true;
        while(it.hasNext())
        {
            Visitor visitor = it.next();
            if(visitor.getName().equals(newVisitor.getName()) && newVisitor.getId() != -1)
            {
                boo = false;
            }
        }
        if(boo )
        {
            registeredVisitors.add(newVisitor);
            int index = registeredVisitors.size();
            newVisitor.setId(index);
        }

        return boo;
    }

    public void addAttraction(Attraction newAttraction)
    {
        attractions.add(newAttraction);
    }

    public Attraction searchByName(String attName)
    {
        Iterator<Attraction> it = attractions.iterator();
        boolean boo = false;
        Attraction att = null;
        while(it.hasNext())
        {
            Attraction attraction = it.next();
            if(attraction.getName().equals(attName))
            {
                boo = true;
                att = attraction;
            }
        }
        return att;
    }

    public int removeVisitors(int startId, int endId)
    {
        Iterator<Visitor> it = registeredVisitors.iterator();
        int count = 0;
        while(it.hasNext())
        {
            Visitor visitor = it.next();
            if(visitor.getId()>= startId)
            {
                if(visitor.getId() <= endId)
                {
                    it.remove();
                    count ++;
                }
            }
        }
        return count;
    }

    public int loadAttractionsFromDB(String dbName)
    {
        Connection c = null;
        try 
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
            System.out.println("Opened database successfully");
            Statement stmt = c.createStatement();
            String q = "SELECT * FROM Attractions";
            System.out.println(q);
            ResultSet rs = stmt.executeQuery( q );
            while ( rs.next() ) 
            {
                int id = rs.getInt("attractionID");
                String name = rs.getString("attName");
                String startT = rs.getString("startTime");
                String endT = rs.getString("endTime");
                int duration = rs.getInt("duration");
                int park = rs.getInt("park");
                LocalTime sTime = LocalTime.parse(startT);
                LocalTime eTime = LocalTime.parse(endT);
                Attraction attraction = new Attraction(name, sTime, eTime, duration);
                if(park == 1)
                {
                    attractions.add(attraction);
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } 
        catch ( Exception e ) 
        {
            System.err.println( "ERROR : "+ e.getMessage() );
        }
        
        return attractions.size();
    }
    
    public int checkReservations(Visitor visitor)
    {
        int count = 0;
        String[] wishList = new String[5];
        if(registeredVisitors.contains(visitor))
        {
            wishList = visitor.getWishList();
            for(int i = 0;i<5;i++)
            {
                String wish = wishList[i];
                Attraction att = this.searchByName(wish);
                if(attractions.contains(att))
                {
                    visitor.addReservation(att,att.getStartTime());
                    count ++;
                    //
                }
            }
            
        }
        return count;
    }
    
    public ArrayList<Attraction> occupiedOn(LocalTime T)
    {
        ArrayList<Attraction> Attractions = new ArrayList<Attraction>();
        for(int i=0;i<attractions.size();i++)
        {
            Attraction attraction = attractions.get(i);
            if(T.isAfter(attraction.getStartTime()) && T.isBefore(attraction.getEndTime()))
            {
                Attractions.add(attraction);
            }
        }
        return Attractions;
    }
    /*		Connection c = null;
    try 
    {
    Class.forName("org.sqlite.JDBC");
    c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
    System.out.println("Opened database successfully");
    Statement stmt = c.createStatement();
    String q = "SELECT...";
    System.out.println(q);
    ResultSet rs = stmt.executeQuery( q );
    while ( rs.next() ) 
    {
    }
    rs.close();
    stmt.close();
    c.close();
    } 
    catch ( Exception e ) 
    {
    System.err.println( "ERROR : "+ e.getMessage() );
    }
     */
}
