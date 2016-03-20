package org.unclazz.metaversion;

import static org.junit.Assert.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.unclazz.metaversion.vo.MaxRevision;
import org.unclazz.metaversion.vo.RevisionRange;

public class RevisionRangeTest {

	@Test
	public void ofBetween_int_int() {
		final RevisionRange rr1_5 = RevisionRange.ofBetween(1, 5); // OK
		assertThat(rr1_5.getStart(), is(1));
		assertThat(rr1_5.getEnd(), is(5));
		assertThat(rr1_5.getEndExclusive(), is(6));
		
		final RevisionRange rr1_1 = RevisionRange.ofBetween(1, 1); // OK
		assertThat(rr1_1.getStart(), is(1));
		assertThat(rr1_1.getEnd(), is(1));
		assertThat(rr1_1.getEndExclusive(), is(2));
		
		final RevisionRange rr5_5 = RevisionRange.ofBetween(5, 5); // OK
		assertThat(rr5_5.getStart(), is(5));
		assertThat(rr5_5.getEnd(), is(5));
		assertThat(rr5_5.getEndExclusive(), is(6));
		
		try {
			RevisionRange.ofBetween(0, 5); // NG
			fail();
		} catch (final IllegalArgumentException e) {
			// OK
		} catch (final Exception e) {
			fail();
		}
		try {
			RevisionRange.ofBetween(0, 0); // NG
			fail();
		} catch (final IllegalArgumentException e) {
			// OK
		} catch (final Exception e) {
			fail();
		}
		try {
			RevisionRange.ofBetween(5, 4); // NG
			fail();
		} catch (final IllegalArgumentException e) {
			// OK
		} catch (final Exception e) {
			fail();
		}
	}

	@Test
	public void iterator() {
		int lastRevision = 0;
		for (final int revision : RevisionRange.ofBetween(1, 1)) {
			lastRevision = revision;
		}
		assertThat(lastRevision, is(1));
		
		for (final int revision : RevisionRange.ofBetween(1, 5)) {
			lastRevision = revision;
		}
		assertThat(lastRevision, is(5));
		
		for (final int revision : RevisionRange.ofBetween(5, 10)) {
			lastRevision = revision;
		}
		assertThat(lastRevision, is(10));
	}

	@Test
	public void withStep_int() {
		final MaxRevision max = MaxRevision.startsWith(0);
		for (final RevisionRange subRange : RevisionRange.ofBetween(1, 5).withStep(1)) {
			assertThat(subRange.getEnd() - subRange.getStart(), is(0));
			assertThat(subRange.getEndExclusive() - subRange.getStart(), is(1));
			max.trySetNewValue(subRange.getEnd());
		}
		assertThat(max.getValue(), is(5));
		
		final List<RevisionRange> rangeList = RevisionRange.ofBetween(10, 35).withStep(10);
		assertThat(rangeList.size(), is(3));
		assertThat(rangeList.get(0).getStart(), is(10));
		assertThat(rangeList.get(0).getEnd(), is(19));
		assertThat(rangeList.get(1).getStart(), is(20));
		assertThat(rangeList.get(1).getEnd(), is(29));
		assertThat(rangeList.get(2).getStart(), is(30));
		assertThat(rangeList.get(2).getEnd(), is(35));
		
		try {
			RevisionRange.ofBetween(10, 35).withStep(0); // NG
			fail();
		} catch (final IllegalArgumentException ex) {
			// OK
		} catch (final Exception e) {
			fail();
		}
	}
}
