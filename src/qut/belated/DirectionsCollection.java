package qut.belated;

import java.util.HashMap;
import java.util.Iterator;

public class DirectionsCollection implements Iterable<Directions> {
	
	HashMap<PhysicalTravelMode, Directions> list;
	public DirectionsCollection()
	{
		list = new HashMap<PhysicalTravelMode, Directions>();
	}
	
	public void clear()
	{
		list.clear();
	}
	
	public void store(Directions directions)
	{
    	list.put(directions.getMode(), directions);
	}

    public Directions retreive(PhysicalTravelMode mode)
    {
    	return list.get(mode);
    }

    public int count()
    {
    	return list.size();
    }

	@Override
	public Iterator<Directions> iterator() {
		return list.values().iterator();
	}
}
