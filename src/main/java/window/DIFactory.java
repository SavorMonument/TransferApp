package window;

import java.util.HashMap;
import java.util.Map;

public class DIFactory
{
	private static DIFactory INSTANCE = new DIFactory();

	private Map<String, Object> instantiatedObjects;

	private DIFactory()
	{
		instantiatedObjects = new HashMap<>();
	}

	public static DIFactory getInstance()
	{
		return INSTANCE;
	}

//	private Object getObject(Class type)
//	{
//		Object object = instantiatedObjects.get(type.getName());
//		if (null != object)
//		{
//			return object;
//		} else
//		{
//			return
//		}
//	}

//	private ConnectionEvents getConnectionEvents()
//	{
//		return (ConnectionEvents) getObject(ConnectionEvents.class);
//	}
}
