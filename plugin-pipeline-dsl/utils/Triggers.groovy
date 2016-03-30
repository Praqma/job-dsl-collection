package utils

class Triggers {
    static def pollScm(def job, String schedule) {
        job.with {
            triggers {
                scm(schedule)
            }
        }
    }
}
