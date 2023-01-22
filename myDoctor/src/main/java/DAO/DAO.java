package DAO;

import java.util.List;

import exceptions.DBException;
import exceptions.InsertionException;
import exceptions.UpdateException;

/**
 * <p>The Data Access Object (DAO) pattern is a structural pattern that allows to
 * isolate the application/business layer from the persistence layer (given by
 * an SQL Relational database) using an abstract API. </p>
 * 
 * <p>This interface defines the standard operations to be performed on a model object.
 * It is used to separate the data persistence logic in a separate layer.</p>
 * 
 * @see https://www.oracle.com/java/technologies/data-access-object.html
 */
public interface DAO<T, S> {

	public T get(int id) throws DBException;
	
	public List<T> getAll(S ex) throws DBException;
	
	public void insert(T item1, S item2 ) throws DBException, InsertionException;
		
	public void update(T item) throws DBException, UpdateException;
	
	public void delete(T item) throws DBException;	
	
	
/**
 * Defines the standard operations to be performed on a model object.
 * It is used to separate the data persistence logic in a separate layer.
 *
 */

	
}
