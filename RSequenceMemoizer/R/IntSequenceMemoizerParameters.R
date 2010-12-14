#' Class IntSequenceMemoizerParameters
#' 
#' Extension for int based Sequence Memoizer.
#' 
#' @slot ismp a edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizerParameters object
setClass ("IntSequenceMemoizerParameters",
		  representation = representation (
				  ismp="jobjRef"))

#' Constructor of IntSequenceMemoizerParameters
#' 
#' @param alphabetSize integer or missing
#' @param depth integer or missing
#' @param maxNumberRestaurants integer or missing
#' @param maxSequenceLength integer or missing
#' @param ... Not used
setGeneric ("IntSequenceMemoizerParameters", function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, ...) standardGeneric ("IntSequenceMemoizerParameters"))

#' Constructor allowing some parameters to be set. The default base distribution is uniform over the types [0,alphabetSize).
#' 
#' @param alphabetSize integer
setMethod ("IntSequenceMemoizerParameters", signature(alphabetSize="integer", depth="missing", maxNumberRestaurants="missing", maxSequenceLength="missing"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, ...) {
			ismp <- .jnew ("edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizerParameters", alphabetSize)
			new ("IntSequenceMemoizerParameters", ismp=ismp)

		})

#' Constructor allowing some parameters to be set. The default base distribution is uniform over the types [0,alphabetSize).
#'
#' @param alphabetSize integer 
#' @param depth integer
setMethod ("IntSequenceMemoizerParameters", signature(alphabetSize="integer", depth="integer", maxNumberRestaurants="missing", maxSequenceLength="missing"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, ...) {
			ismp <- .jnew ("edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizerParameters", depth, alphabetSize)
			new ("IntSequenceMemoizerParameters", ismp=ismp)
		})

#' Constructor allowing some parameters to be set. The default base distribution is uniform over the types [0,alphabetSize).
#' 
#' @param alphabetSize integer
#' @param depth integer
#' @param maxNumberRestaurants integer
#' @param maxSequenceLength integer
setMethod ("IntSequenceMemoizerParameters", signature(alphabetSize="integer", depth="integer", maxNumberRestaurants="integer", maxSequenceLength="integer"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, ...) {
			ismp <- .jnew ("edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizerParameters", depth, .jlong(maxNumberRestaurants), .jlong(maxSequenceLength), alphabetSize)
			new ("IntSequenceMemoizerParameters", ismp=ismp)
		})
