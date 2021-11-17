/*
 * Copyright Splunk Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.splunk.android.workshopapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;

import com.splunk.android.workshopapp.databinding.FragmentSecondBinding;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SecondFragment extends Fragment {

    private final ScheduledExecutorService spammer = Executors.newSingleThreadScheduledExecutor();
    private final MutableLiveData<String> spanCountLabel = new MutableLiveData<>();
    private final AtomicLong spams = new AtomicLong(0);

    private ScheduledFuture<?> spamTask;

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setSecondFragment(this);

        resetLabel();

        binding.buttonSecond.setOnClickListener(v -> {
            //an example of using the OpenTelemetry API directly to generate a 100% custom span.
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });
        binding.buttonToWebview.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_webViewFragment));

        binding.buttonToCustomTab.setOnClickListener(v -> {
            String url = "https://pmrum.o11ystore.com/";
            new CustomTabsIntent.Builder()
                    .setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)
                    .setStartAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .setExitAnimations(getContext(), android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                    .build()
                    .launchUrl(this.getContext(), Uri.parse(url));
        });

        binding.buttonSpam.setOnClickListener(v -> toggleSpam());

        binding.buttonFreeze.setOnClickListener(v -> {
            try {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(1_000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        binding.buttonWork.setOnClickListener(v -> {
            var random = new Random();
            long startTime = System.currentTimeMillis();
            do {
                random.nextDouble();
            } while (System.currentTimeMillis() - startTime <= 20_000);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public LiveData<String> getSpanCountLabel() {
        return spanCountLabel;
    }

    private void toggleSpam() {
        if (spamTask == null) {
            resetLabel();
            spamTask = spammer.scheduleAtFixedRate(this::createSpam, 0, 50, TimeUnit.MILLISECONDS);
            binding.buttonSpam.setText(R.string.stop_spam);
        } else {
            spamTask.cancel(false);
            spamTask = null;
            binding.buttonSpam.setText(R.string.start_spam);
        }
    }

    private void resetLabel() {
        spams.set(0);
        updateLabel();
    }

    private void updateLabel() {
        spanCountLabel.postValue(getString(R.string.spam_status, spams.get()));
    }

    private void createSpam() {
        spams.incrementAndGet();
        updateLabel();
    }
}
