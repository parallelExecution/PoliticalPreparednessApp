package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*

@Dao
interface ElectionDao {

    /**
     * Insert a election in the database. If the election already exists, ignore it.
     *
     * @param Election the election to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertElection(election: Election)

    /**
     * Insert all upcoming elections in the database. If the election already exists, ignore it.
     *
     * @param elections the elections to be inserted as a typed array
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllElections(elections: List<Election>)

    /**
     * Update the followed status of a election
     *
     * @param electionId id of the election
     * @param followed status to be updated
     */
    @Query("UPDATE election_table SET followed = :followed WHERE id = :electionId")
    suspend fun updateElection(electionId: Int, followed: Boolean)

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Observes a single election.
     *
     * @param electionId the election id.
     * @return the election with electionId.
     */
    @Query("SELECT * FROM election_table WHERE id = :electionId")
    fun observeElectionById(electionId: Int): LiveData<Election>

    /**
     * Observes list of elections.
     *
     * @return all elections.
     */
    @Query("SELECT * FROM election_table ORDER BY electionDay")
    fun observeElections(): LiveData<List<Election>>

    /**
     * Observes list of followed elections.
     *
     * @return all followed elections.
     */
    @Query("SELECT * FROM election_table WHERE followed = :followed ORDER BY electionDay")
    fun observeFollowedElections(followed: Boolean): LiveData<List<Election>>

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Select an election by id.
     *
     * @param electionId the election id.
     * @return the election with electionId.
     */
    @Query("SELECT * FROM election_table WHERE id = :electionId")
    suspend fun getElectionById(electionId: Int): Election?

    /**
     * Select all elections from the election table.
     *
     * @return all elections.
     */
    @Query("SELECT * FROM election_table ORDER BY electionDay")
    suspend fun getElections(): List<Election>

    /**
     * Select all followed elections from the election table
     *
     * @return all followed election.
     */
    @Query("SELECT * FROM election_table WHERE followed = :followed ORDER BY electionDay")
    suspend fun getFollowedElections(followed: Boolean): List<Election>

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Delete an election by id.
     *
     * @return the number of elections deleted. This should always be 1.
     */
    @Query("DELETE FROM election_table WHERE id = :electionId")
    suspend fun deleteElectionById(electionId: Int): Int

    /**
     * Delete all elections whose electionDay is before current day
     */
    @Query("DELETE FROM election_table WHERE electionDay < :currentDate")
    suspend fun deletePastElections(currentDate: Date)

    /**
     * Delete all elections.
     */
    @Query("DELETE FROM election_table")
    suspend fun deleteElections()

}