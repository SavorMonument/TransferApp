package window.handle;

import java.util.HashMap;
import java.util.Map;

public class UIBeanRepository
{
	private static UIBeanRepository INSTANCE = new UIBeanRepository();

	private Map<String, UIBean> instantiatedObjects;

	private UIBeanRepository()
	{
		instantiatedObjects = new HashMap<>();
	}

	public static UIBeanRepository getInstance()
	{
		return INSTANCE;
	}

	public void add(UIBean object)
	{
		instantiatedObjects.put(object.getClass().getName(), object);
	}

	public <T> T getUIBean(Class type)
	{
		return (T) instantiatedObjects.get(type.getName());
	}

	public interface UIBean
	{

	}


}
