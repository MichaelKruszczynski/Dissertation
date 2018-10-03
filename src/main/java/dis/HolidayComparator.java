package dis;

import java.util.Comparator;

public class HolidayComparator implements Comparator {

	private static final int FIRST = -1;
	private static final int LAST = 1;

	@Override
	public int compare(Object hol1, Object hol2) {
		return compare((Holiday) hol1, (Holiday) hol2);
	}

	public int compare(Holiday hol1, Holiday hol2) {
		if (hol1.getEmployee().getId() > hol2.getEmployee().getId()) {
			return FIRST;
		} else if (hol1.getEmployee().getId() < hol2.getEmployee().getId()) {
			return LAST;
		}

		if (hol1.getDay().before(hol2.getDay())) {
			return FIRST;
		} else if (hol1.getDay().after(hol2.getDay())) {
			return LAST;
		}
		return 0;
	}

}
