\name{RSequenceMemoizer-package}
\docType{package}
\alias{RSequenceMemoizer-package}
\alias{RSequenceMemoizer}
\title{SequenceMemoizer called from R}
\description{ The \pkg{RSequenceMemoizer} package provides an
  R interface for calling an implementation of a Sequence Memoizer
  written in Java.
}
\author{
	Daniel Lee <bearlee@alum.mit.edu> 
}
\examples{
	## Adapted from example 1: http://www.sequencememoizer.com/matlab/index.html
	
	library (RSequenceMemoizer)
	
	gettysburg_address <- paste ("Four score and seven years ago our fathers ",
	       "brought forth on this continent, a new nation, conceived in Liberty,",
	       "and dedicated to the proposition that all men are created equal.  Now ",
	       "we are engaged in a great civil war, testing whether that nation, ",
	       "or any nation so conceived and so dedicated, can long endure. We ",
	       "are met on a great battle-field of that war. We have come to ",
	       "dedicate a portion of that field, as a final resting place for ",
	       "those who here gave their lives that that nation might live. It is ",
	       "altogether fitting and proper that we should do this. But, in a ",
	       "larger sense, we can not dedicate -- we can not consecrate -- we can ",
	       "not hallow -- this ground. The brave men, living and dead, who ",
	       "struggled here, have consecrated it, far above our poor power to add ",
	       "or detract. The world will little note, nor long remember what we ",
	       "say here, but it can never forget what they did here. It is for us ",
	       "the living, rather, to be dedicated here to the unfinished work which ",
	       "they who fought here have thus far so nobly advanced. It is rather for ",
	       "us to be here dedicated to the great task remaining before us -- that ",
	       "from these honored dead we take increased devotion to that cause for ",
	       "which they gave the last full measure of devotion -- that we here highly ",
	       "resolve that these dead shall not have died in vain -- that this nation, ",
	       "under God, shall have a new birth of freedom -- and that government of ",
	       "the people, by the people, for the people, shall not perish from the earth.", sep="")
	   int_gettysburg_address <- as.integer (charToRaw (gettysburg_address))
	   vocab_size <- as.integer (max(int_gettysburg_address)+1)  # sequence memoizer is 0-based, not 1-based
	
	sm <- IntSequenceMemoizer (vocab_size)
	
	sm$continueSequence(int_gettysburg_address);
	num_samples <- 100;
	sm$sample(num_samples)
	
	
	# what would Lincoln have said if the address were to have continued
	forwardSample <- sm$generateSequence(int_gettysburg_address, 100L);
	
	forwardSampleText <- intToUtf8(forwardSample);
	forwardSampleText
}
\references{
	\url{http://www.sequencememoizer.com}
}