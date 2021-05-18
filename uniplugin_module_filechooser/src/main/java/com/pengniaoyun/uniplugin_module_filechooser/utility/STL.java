package com.pengniaoyun.uniplugin_module_filechooser.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class STL
{
	private STL(){}

	public static <T, U> int MapRemoveValue(Map<T, U> map, U value)
	{
		if(map == null)
			return -1;
		if(map.isEmpty())
			return 0;
		Set<T> keys = new HashSet<>();
		for(Iterator<Map.Entry<T, U> > itor = map.entrySet().iterator();
			itor.hasNext();
		)
		{
			Map.Entry<T, U> entry = itor.next();
			if(entry.getValue() == value)
				keys.add(entry.getKey());
		}

		int res = 0;
		for(T t : keys)
		{
			if(map.containsKey(t))
				res++;
			map.remove(t);
		}

		return res;
	}

	public static String CollectionJoin(Collection list, String ch)
	{
		if(list == null)
			return null;

		StringBuffer sb = new StringBuffer();
		int i = 0;
		Iterator itor = list.iterator();
		while(itor.hasNext())
		{
			sb.append(itor.next().toString());
			if(itor.hasNext())
				sb.append(ch);
		}
		return sb.toString();
	}

	public static <T> int CollectionToArray(Collection list, T ret[])
	{
		if(list == null)
			return -1;

		int i = 0;
		Iterator itor = list.iterator();
		while(itor.hasNext())
		{
			if(i >= ret.length)
				break;
			ret[i++] = Common.dynamic_cast(itor.next());
		}
		return i;
	}

	public static boolean CollectionIsEmpty(Collection list)
	{
		return list == null || list.isEmpty();
	}
}
