package cm.aptoide.pt.app.migration

import rx.Observable
import java.util.*

/**
 * This class is open just because of Mockito compat.
 * Mockito 2 does not have this restriction however.
 */
open class AppcMigrationRepository(val appcMigrationPersistence: AppcMigrationPersistence) {

  private val migrationCandidates = ArrayList<String>()

  fun addMigrationCandidate(packageName: String) {
    if (!migrationCandidates.contains(packageName)) {
      migrationCandidates.add(packageName)
    }
  }

  fun persistCandidate(packageName: String) {
    if (migrationCandidates.contains(packageName)) {
      appcMigrationPersistence.insert(packageName)
      migrationCandidates.remove(packageName)
    }
  }

  fun isAppMigrated(packageName: String): Observable<Boolean> {
    return appcMigrationPersistence.isAppMigrated(packageName)
  }
}