/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.ground.ui.datacollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.google.android.ground.model.submission.Submission
import com.google.android.ground.repository.SubmissionRepository
import com.google.android.ground.rx.annotations.Hot
import com.google.android.ground.ui.common.AbstractViewModel
import com.google.android.ground.ui.common.LocationOfInterestHelper
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import java8.util.Optional
import javax.inject.Inject

/**
 * View model for the Data Collection fragment.
 */
class DataCollectionViewModel @Inject internal constructor(
    private val submissionRepository: SubmissionRepository,
    private val locationOfInterestHelper: LocationOfInterestHelper
) : AbstractViewModel() {
    val submission: @Hot(replays = true) LiveData<Submission?>
    val jobName: @Hot(replays = true) LiveData<String>
    val loiName: @Hot(replays = true) LiveData<String>

    private val argsProcessor: @Hot(replays = true) FlowableProcessor<DataCollectionFragmentArgs> =
        BehaviorProcessor.create()

    init {
        val submissionStream: Flowable<Submission?> =
            argsProcessor.switchMapSingle { args ->
                submissionRepository.createSubmission(
                    args.surveyId, args.locationOfInterestId, args.submissionId
                ).onErrorReturn { null }
            }

        submission = LiveDataReactiveStreams.fromPublisher(submissionStream)

        jobName = LiveDataReactiveStreams.fromPublisher(submissionStream.map {
            it.locationOfInterest.job.name ?: ""
        })

        loiName = LiveDataReactiveStreams.fromPublisher(submissionStream.map {
            it.locationOfInterest
        }.map { locationOfInterest ->
            locationOfInterestHelper.getLabel(
                Optional.ofNullable(locationOfInterest)
            )
        })
    }

    fun loadSubmissionDetails(
        args: DataCollectionFragmentArgs
    ) = argsProcessor.onNext(args)
}