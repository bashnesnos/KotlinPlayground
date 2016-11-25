import org.HdrHistogram.Histogram
import java.io.BufferedInputStream
import java.io.File
import java.io.PrintStream

/**
 * Created by ASemelit on 25.11.2016.
 */

fun fillHistrogram(accessLog: File, addBundlesServiceTimeHistogram: Histogram, availableBundlesServiceTimeHistogram: Histogram) {
    println("Access log ${accessLog.canonicalPath} analysis started")
    BufferedInputStream(accessLog.inputStream()).reader().use {
        it.forEachLine { line ->
            //parse line
            var significantPart = line.substring(line.lastIndexOf("/") + 1)
            var serviceTime = significantPart.substring(significantPart.lastIndexOf(" ") + 1).toLong()
            if (significantPart.startsWith("G")) {
                availableBundlesServiceTimeHistogram.recordValue(serviceTime)
            } else {
                addBundlesServiceTimeHistogram.recordValue(serviceTime)
            }
        }
    }
}


fun main(args: Array<String>) {
    println("Access logs ${args.asList()} for analysis")
    val addBundlesServiceTimeHistogram = Histogram(5)
    val availableBundlesServiceTimeHistogram = Histogram(5)

    args.forEach { accessLog ->
        var accessLogFile = File(accessLog)
        if (accessLogFile.isDirectory) {
            accessLogFile.walkTopDown().forEach {
                if (it.isFile) {
                    fillHistrogram(it, addBundlesServiceTimeHistogram, availableBundlesServiceTimeHistogram)
                }
            }
        } else {
            fillHistrogram(accessLogFile, addBundlesServiceTimeHistogram, availableBundlesServiceTimeHistogram)
        }
    }

    PrintStream(File("C:\\Users\\asemelit\\performance\\PMFS\\availableBundlesHistogram.txt")).use {
        availableBundlesServiceTimeHistogram.outputPercentileDistribution(it, 10, 1.0)
    }

    PrintStream(File("C:\\Users\\asemelit\\performance\\PMFS\\addBundlesHistogram.txt")).use {
        addBundlesServiceTimeHistogram.outputPercentileDistribution(it, 10, 1.0)
    }
}