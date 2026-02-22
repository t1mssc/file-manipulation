package helpers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogAnalysisHelper {
    static final LOG_PATTERN = /^(\d{4}-\d{2}-\d{2})\s(\d{2}:\d{2}:\d{2})\s(\w+)\s\[([^\]]+)\]\s(.+)$/

    static def parseLogs(File file) {
        def entries = []
        file.eachLine { line ->
            def matcher = line =~ LOG_PATTERN
            if (matcher.matches()) {
                entries << [
                        date: matcher[0][1],
                        timestamp: matcher[0][2],
                        level: matcher[0][3],
                        thread: matcher[0][4],
                        message: matcher[0][5]
                ]
            }
        }
        return entries
    }

    static def perfAnalyzer(List entries) {
        return [
                slowQueries: entries.findAll { it.message.contains('Slow query') },
                memoryWarnings: entries.findAll {
                    def matcher = it.message =~ /Memory usage: (\d+)%/
                    if (matcher.find()) return matcher[0][1].toInteger() > 80
                    return false
                },
                apiTimeouts: entries.findAll { it.message.toLowerCase().contains('timeout') }
        ]
    }

    static def makeRecommendations(Map issues) {
        def recommend = []

        if (issues.slowQueries?.size() > 0) {
            recommend << 'Optimize database queries'
            recommend << 'Consider adding database indexes'
        }

        if (issues.memoryWarnings?.size() > 0) {
            recommend << 'Investigate memory leaks'
            recommend << 'Consider increasing heap size'
        }

        if (issues.apiTimeouts?.size() > 0) {
            recommend << 'Review API timeout settings'
            recommend << 'Implement retry logic with exponential backoff'
        }
        return recommend
    }

    static def generateReport (List errorEntries) {
        if (!errorEntries || errorEntries.isEmpty()) return 'No entries found'

        def report = new StringBuilder()
        def timeStamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss'))

        def groupedBy = errorEntries.groupBy { it.level }
        def logLevel = ['FATAL', 'ERROR', 'WARN', 'INFO']

        def totalFatal = groupedBy['FATAL']?.size() ?: 0
        def totalError = groupedBy['ERROR']?.size() ?: 0
        def totalWarn = groupedBy['WARN']?.size() ?: 0
        def totalInfo = groupedBy['INFO']?.size() ?: 0

        report << '============================================================\n'
        report << '               ERROR ANALYSIS REPORT                                  \n'
        report << '============================================================\n'
        report << "Generated at : ${timeStamp}\n"
        report << "Total Errors : ${errorEntries.size()}\n"
        report << '------------------------------------------------------------\n\n'
        report << "FATAL: ${totalFatal}\n"
        report << "ERROR: ${totalError}\n"
        report << "WARN: ${totalWarn}\n"
        report << "INFO: ${totalInfo}\n"
        report << '============================================================\n\n'

        logLevel.each { level ->
            def entries = groupedBy[level]
            if (entries) {
                def icon = level == 'FATAL' ? '💀' :
                        level == 'ERROR' ? '❌' :
                                level == 'WARN'  ? "⚠️" : "ℹ️"

                report << "${icon} [ ${level} ] - ${entries.size()} occurrence(s)\n"
                report << '------------------------------------------------------------\n'
                entries.each { entry ->
                    report << "  Date      : ${entry.date}\n"
                    report << "  Timestamp : ${entry.timestamp}\n"
                    report << "  Thread    : ${entry.thread}\n"
                    report << "  Message   : ${entry.message}\n"
                    report << '\n'
                }
            }
        }
        report << '============================================================\n'
        report << '                     END OF REPORT                         \n'
        report << '============================================================\n'

        return report.toString()
    }
}
