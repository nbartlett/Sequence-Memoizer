#' Class IntSequenceMemoizer
#'
#' Implementation of a sequence memoizer which takes int types. 
#'
#' @param sm a edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer object
#' @param ismp a edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizerParameters object
setRefClass ("IntSequenceMemoizer",
	contains = "IntSequenceMemoizerInterface")

		  
		  
#' Constructor of IntSequenceMemoizer
#' 
#' @param parameters IntSequenceMemoizer object
#' @param ... Not used
setGeneric ("IntSequenceMemoizer", function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, parameters, ...) standardGeneric ("IntSequenceMemoizer"))

#' Constructor initiating the model with the specified parameters.
#' 
#' @param parameters parameters for the model
setMethod ("IntSequenceMemoizer", signature(alphabetSize="missing", depth="missing", maxNumberRestaurants="missing", maxSequenceLength="missing", 
				parameters="IntSequenceMemoizerParameters"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, parameters, ...) {
			sm <- .jnew ("edu.columbia.stat.wood.pub.sequencememoizer.IntSequenceMemoizer", parameters@ismp);
			new("IntSequenceMemoizer", sm=sm, parameters=parameters)
		})

#' Constructor allowing some parameters to be set. The default base distribution is uniform over the types [0,alphabetSize).
#' 
#' @param alphabetSize integer
setMethod ("IntSequenceMemoizer", signature(alphabetSize="integer", depth="missing", maxNumberRestaurants="missing", maxSequenceLength="missing", parameters="missing"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, parameters, ...) {
			return (IntSequenceMemoizer (parameters=IntSequenceMemoizerParameters (alphabetSize)))
		})

#' Constructor allowing some parameters to be set. The default base distribution is uniform over the types [0,alphabetSize).
#'
#' @param alphabetSize integer 
#' @param depth integer
setMethod ("IntSequenceMemoizer", signature(alphabetSize="integer", depth="integer", maxNumberRestaurants="missing", maxSequenceLength="missing", parameters="missing"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, parameters, ...) {
			return (IntSequenceMemoizer (parameters=IntSequenceMemoizerParameters (alphabetSize, depth)))
		})

#' Constructor allowing some parameters to be set. The default base distribution is uniform over the types [0,alphabetSize).
#' 
#' @param alphabetSize integer
#' @param depth integer
#' @param maxNumberRestaurants integer
#' @param maxSequenceLength integer
setMethod ("IntSequenceMemoizer", signature(alphabetSize="integer", depth="integer", maxNumberRestaurants="integer", maxSequenceLength="integer", parameters="missing"),
		function (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, parameters, ...) {
			return (IntSequenceMemoizer (parameters=IntSequenceMemoizerParameters (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength)))
		})