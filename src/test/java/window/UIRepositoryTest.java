package window;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import window.handle.UIBeanRepository;

import static org.junit.Assert.*;

public class UIRepositoryTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void add()
	{
		UIBeanRepository repository = UIBeanRepository.getInstance();

		repository.add(new TestClass());
	}

	@Test
	public void getObject()
	{
		TestClass test = new TestClass();
		UIBeanRepository repository = UIBeanRepository.getInstance();

		repository.add(test);

		assertEquals(test, repository.getUIBean(TestClass.class));
	}

	private class TestClass implements UIBeanRepository.UIBean
	{}
}