#' Class IntSequenceMemoizerInterface
#'
#' Implementation of a sequence memoizer which takes int types. 
#'
#' @param sm a edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer object
#' @param ismp a edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizerParameters object
setRefClass ("IntSequenceMemoizerInterface",
	fields = list (sm = "jobjRef", parameters = "IntSequenceMemoizerParameters"),
	methods = list (
		continueSequence = function (observations) {
			observations <- .as.integer (observations)
			return (.jcall (obj=sm, returnSig="D", method="continueSequence", .jarray (observations)))
		},
		generate = function (context, numSamples) {
			context <- .as.integer(context)
			numSamples <- .as.integer(numSamples)
			return (.jcall (obj=sm, returnSig="[I", method="generate", .jarray (context), numSamples))
		},
		generateSequence = function (context, sequenceLength) {
			context <- .as.integer(context)
			sequenceLength <- .as.integer(sequenceLength)
			return (.jcall (obj=sm, returnSig="[I", method="generateSequence", .jarray (context), sequenceLength))
		}, 
		getParameters = function () {
			return (parameters)
		},
		newSequence = function () {
			return (.jcall (obj=sm, returnSig="V", method="newSequence"))
		},
		## For some reason, rJava can not resolve IntDiscreteDistribution
		predictiveDistribution = function (context) {
			context <- .as.integer (context)
			#return (.jcall (obj=sm, returnSig="Ledu/columbia/stat/wood/pub/util/IntDiscreteDistribution", method="predictiveDistribution", .jarray (context)))
			return (sm$predictiveDistribution (.jarray (context)))
		},
		predictiveProbability = function (context, type) {
			context <- .as.integer(context)
			type <- .as.integer (type)
			return (.jcall (obj=sm, returnSig="D", method="predictiveProbability", .jarray (context), type))
		},
		sample = function (numSweeps) {
			numSweeps <- .as.integer (numSweeps)
			return (.jcall (obj=sm, returnSig="D", method="sample", numSweeps))
		},
		sampleDiscounts = function (numSweeps) {
			numSweeps <- .as.integer (numSweeps)
			return (.jcall (obj=sm, returnSig="D", method="sampleDiscounts", numSweeps))
		},
		sampleSeatingArrangements = function (numSweeps) {
			numSweeps <- .as.integer (numSweeps)
			return (.jcall (obj=sm, returnSig="V", method="sampleSeatingArrangements", numSweeps))
		},
		score = function () {
			return (.jcall (obj=sm, returnSig="D", method="score"))
		},
		sequenceProbability = function (context, sequence) {
			context <- .as.integer(context)
			sequence <- .as.integer (sequence)
			return (.jcall (obj=sm, returnSig="D", method="sequenceProbability", .jarray (context), .jarray (sequence)))
		}
	)
)