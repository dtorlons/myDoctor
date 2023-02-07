package it.myDoctor.model.DAO;

import java.util.List;

import it.myDoctor.controller.exceptions.DBException;
import it.myDoctor.controller.exceptions.InsertionException;
import it.myDoctor.controller.exceptions.UpdateException;

/**
 *
 * The DAO interface defines the basic methods for data access objects.
 *
 * It provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on a given table in the database.
 *
 *
 * @param <T> the type of the object to be retrieved, inserted, updated, or
 *            deleted
 *
 * @param <S> the type of the second object required for insertion, relative to
 *            the association between the two
 *
 *
 * @author Diego Torlone
 *
 * @see https://www.oracle.com/java/technologies/data-access-object.html
 */
public interface DAO<T, S> {

	/**
	 *
	 * Retrieve a single object from the database by id.
	 *
	 * @param id the id of the object to be retrieved
	 * @return the object matching the id
	 * @throws DBException if there is an error in the database operation
	 */
	public T get(int id) throws DBException;

	/**
	 *
	 * Retrieve a list of objects from the database based on an example object.
	 *
	 * @param ex the example object to be used for the query
	 * @return the list of objects matching the example
	 * @throws DBException if there is an error in the database operation
	 */
	public List<T> getAll(S ex) throws DBException;

	/**
	 *
	 * Insert a single object into the database.
	 *
	 * @param item1 the object to be inserted
	 * @param item2 the second object required for the insertion
	 * @throws DBException        if there is an error in the database operation
	 * @throws InsertionException if there is a violation of a business rule during
	 *                            insertion
	 */
	public void insert(T item1, S item2) throws DBException, InsertionException;

	/**
	 *
	 * Update a single object in the database.
	 *
	 * @param item the object to be updated
	 * @throws DBException     if there is an error in the database operation
	 * @throws UpdateException if there is a violation of a business rule during
	 *                         update
	 */
	public void update(T item) throws DBException, UpdateException;

	/**
	 *
	 * Delete a single object from the database.
	 *
	 * @param item the object to be deleted
	 * @throws DBException if there is an error in the database operation
	 */
	public void delete(T item) throws DBException;

}
