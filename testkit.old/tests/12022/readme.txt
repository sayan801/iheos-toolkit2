R.b Accept document replace, addendum or transformation present

This test validates the rules spelled out in CP117 covering the rules for RPLC/XFRM/APND

submit
	step submit - submit 2 documents (doc01, doc02) and a folder, doc02 is in folder
	step xfrm_apnd - add doc05 as XFRM of doc01, add doc06 as APND of doc01

apnd
	step submit - submit 2 docs (Document01 and Document02). Document01 is APND to doc01
		and Document02 is XFRM to doc01

rplc
	step rplc - submit 1 doc, Document01 which is RPLC to doc01 in submit

eval
	step original_deprecated - Issues a GetSubmissionSetAndContents query with the
		SubmissionSet.entryUUID of the submission in section submit, step submit.
		The key test is that Doc1 is deprecated since it was replaced.
	apnd_xfrm_deprecated  - Issues a GetSubmissionSetAndContents query with the
		SubmissionSet.entryUUID of the submission in section apnd.
		The key test is that Doc1 was deprecated to all APND and XFRM documents
		hanging off it must be also deprecated.
	replacement_approved