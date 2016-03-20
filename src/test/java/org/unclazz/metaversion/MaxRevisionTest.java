package test.org.unclazz.metaversion;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.unclazz.metaversion.vo.MaxRevision;

public class MaxRevisionTest {

	@Test
	public void startsWith_int() {
		MaxRevision.startsWith(0); // OK
		MaxRevision.startsWith(1); // OK
		MaxRevision.startsWith(10); // OK
		try {
			MaxRevision.startsWith(-1); // NG
			fail();
		} catch (final IllegalArgumentException e) {
			// OK
		} catch (final Exception e) {
			fail();
		}
	}

	@Test
	public void trySetNewValue_int() {
		final MaxRevision mr0 = MaxRevision.startsWith(0);
		assertThat(mr0.getValue(), is(0));
		mr0.trySetNewValue(1);
		assertThat(mr0.getValue(), is(1));
		mr0.trySetNewValue(10);
		assertThat(mr0.getValue(), is(10));
		mr0.trySetNewValue(9);
		assertThat(mr0.getValue(), is(10));
	}

}
