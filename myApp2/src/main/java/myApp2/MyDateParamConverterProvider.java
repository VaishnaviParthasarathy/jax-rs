package myApp2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class MyDateParamConverterProvider implements ParamConverterProvider {

	@Override
	public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
		
		if (rawType.getName().equalsIgnoreCase(MyDate.class.getName()))
		{
			return new ParamConverter<T>(){

				@Override
				public T fromString(String value) {
					Calendar date=Calendar.getInstance();
					if(value.equalsIgnoreCase("tomorrow"))
						date.add(Calendar.DATE, 1);
					else if(value.equalsIgnoreCase("yesterday"))
						date.add(Calendar.DATE, -1);
					
					MyDate mydate=new MyDate();
					mydate.setDate(date.get(Calendar.DATE));
					mydate.setMonth(date.get(Calendar.MONTH));
					mydate.setYear(date.get(Calendar.YEAR));
					return rawType.cast(mydate);
				}

				@Override
				public String toString(T value) {
					if(value==null)
					return null;
					
					return value.toString();
				}
				
			};
		}
		
		return null;
	}

}
