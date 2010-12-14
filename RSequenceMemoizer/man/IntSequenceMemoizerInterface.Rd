\name{IntSequenceMemoizerInterface-class}
\docType{class}
\alias{IntSequenceMemoizerInterface-class}
\alias{continueSequence,IntSequenceMemoizerInterface-method}
\alias{generate,IntSequenceMemoizerInterface-method}
\alias{generateSequence,IntSequenceMemoizerInterface-method}
\alias{getParameters,IntSequenceMemoizerInterface-method}
\alias{newSequence,IntSequenceMemoizerInterface-method}
\alias{predictiveDistribution,IntSequenceMemoizerInterface-method}
\alias{predictiveProbability,IntSequenceMemoizerInterface-method}
\alias{sample,IntSequenceMemoizerInterface-method}
\alias{sampleDiscounts,IntSequenceMemoizerInterface-method}
\alias{sampleSeatingArrangements,IntSequenceMemoizerInterface-method}
\alias{score,IntSequenceMemoizerInterface-method}
\alias{sequenceProbability,IntSequenceMemoizerInterface-method}

\title{IntSequenceMemoizerInterface}
\description{
  Sequence memoizer interface for int types.
}
\section{Methods}{
  \describe{
    \item{continueSequence}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Continue the sequence with the sequence of observations supplied.}
    \item{generate}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Generate some number of samples given a specified context.}
    \item{generateSequence}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Generate a sequence from the predictive model, starting with the specified context.}
    \item{getParameters}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Get the parameters of this model.}
    \item{newSequence}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Indicates to the model that you wish to start a new sequence.}
    \item{predictiveDistribution}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Get the predictive distribution in a specified context.}
    \item{predictiveProbability}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Get the predictive probability of a given type in a given context.}
    \item{sample}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Sample the parameters in the model.}
    \item{sampleDiscounts}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Sample only the discounts.}
    \item{sampleSeatingArrangements}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Sample only the seating arrangements.}
    \item{score}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Get the joint log probability of model and data.}
    \item{sequenceProbability}{\code{signature(object = "IntSequenceMemoizerInterface")}: 
      Gets the log probability of a sequence given the current state of the model.}
  }
}
\references{
  \url{http://www.sequencememoizer.com/documentation/sequencememoizer/index.html?edu/columbia/stat/wood/pub/sequencememoizer/IntSequenceMemoizerInterface.html}
}

