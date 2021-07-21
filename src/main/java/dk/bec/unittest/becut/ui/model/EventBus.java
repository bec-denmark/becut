package dk.bec.unittest.becut.ui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
	Map<Class<? extends Object>, List<Consumer<Object>>> listeners = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> void register(Class<T> klass, Consumer<T> consumer) {
		if(!listeners.containsKey(klass)) {
			listeners.put(klass, new ArrayList<>());
		}
		listeners.get(klass).add((Consumer<Object>)consumer);
	}

	public void post(Object event) {
		if(event == null) return;
		Class<? extends Object> klass = event.getClass();
		if(listeners.containsKey(klass)) {
			listeners.get(klass)
				.forEach(consumer -> consumer.accept((Object)event));
		} else {
			System.err.printf("No listener for a %s event\n", event.getClass().getName());
		}
	}
}
