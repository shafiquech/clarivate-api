package com.example.filedemo.mapper;

public interface Mapper<V, E> {

	public V mapToVo(E e);

	public E mapToEntity(V v);

}
