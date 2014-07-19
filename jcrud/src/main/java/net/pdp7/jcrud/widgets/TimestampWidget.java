package net.pdp7.jcrud.widgets;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.springframework.web.context.request.WebRequest;

import com.hp.gagawa.java.elements.Input;
import com.hp.gagawa.java.elements.Option;
import com.hp.gagawa.java.elements.Select;

public class TimestampWidget extends DefaultWidget {

	protected String dateFormatStr = "yyyy-MM-dd";

	@Override
	public String render(String name, Object value) {
		Timestamp timestamp = (Timestamp) value;

		GregorianCalendar calendar = null;

		if(timestamp != null) {
			calendar = new GregorianCalendar();
			calendar.setTime(timestamp);
		}

		String out = "";
		out += new Input()
			.setValue(value == null ? dateFormatStr : new SimpleDateFormat(dateFormatStr).format(timestamp))
			.setName(name + "_date")
			.setCSSClass("date_picker")
			.write();

		out += createSelect(name, "_hour", 24, calendar == null ? 0 : calendar.get(GregorianCalendar.HOUR_OF_DAY)).write();
		out += createSelect(name, "_minute", 60, calendar == null ? 0 : calendar.get(GregorianCalendar.MINUTE)).write();
		out += createSelect(name, "_second", 60, calendar == null ? 0 : calendar.get(GregorianCalendar.SECOND)).write();

		out += new Input()
			.setValue(value == null ? "000" : new SimpleDateFormat("SSS").format(timestamp))
			.setName(name + "_millisecond")
			.setMaxlength("3")
			.setSize("3")
			.write();

		return out;
	}

	protected Select createSelect(String name, String suffix, int maxElements, int selected) {
		Select select = new Select().setName(name + suffix);

		for(int i=0; i<maxElements; i++) {
			Option option = new Option()
				.setValue(Integer.toString(i))
				.appendText(new DecimalFormat("00").format(i));

			if(i == selected) {
				option.setSelected("selected");
			}

			select.appendChild(option);
		}
		return select;
	}

	@Override
	public Object parseFromRequest(WebRequest request, String name) {
		try {
			GregorianCalendar result = new GregorianCalendar();
			result.setTime(new SimpleDateFormat(dateFormatStr).parse(request.getParameter(name + "_date")));
			result.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(request.getParameter(name + "_hour")));
			result.set(GregorianCalendar.MINUTE, Integer.parseInt(request.getParameter(name + "_minute")));
			result.set(GregorianCalendar.SECOND, Integer.parseInt(request.getParameter(name + "_second")));
			result.set(GregorianCalendar.MILLISECOND, Integer.parseInt(request.getParameter(name + "_millisecond")));
			return new Timestamp(result.getTimeInMillis());
		}
		catch(ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
