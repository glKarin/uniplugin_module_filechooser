package com.pengniaoyun.uniplugin_module_filechooser.utility;

public/* final*/ class Pair<T, U>
{
	public T first;
	public U second;

	public Pair(){}
	public Pair(T t, U u)
	{
		first = t;
		second = u;
	}

	public static<T, U> Pair<T, U> make_pair(T t, U u)
	{
		return new Pair<T, U>(t, u);
	}
}
