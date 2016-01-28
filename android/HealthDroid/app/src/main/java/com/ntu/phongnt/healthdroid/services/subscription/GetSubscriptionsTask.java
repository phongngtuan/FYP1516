//package com.ntu.phongnt.healthdroid.services.subscription;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.ntu.phongnt.healthdroid.data.subscription.Subscription;
//import com.ntu.phongnt.healthdroid.data.subscription.model.SubscriptionRecord;
//import com.ntu.phongnt.healthdroid.services.SubscriptionFactory;
//import com.ntu.phongnt.healthdroid.subscription.UserFragment;
//
//import java.io.IOException;
//import java.util.List;
//
//public class GetSubscriptionsTask extends AsyncTask<Void, Void, List<SubscriptionRecord>> {
//    @Override
//    protected List<SubscriptionRecord> doInBackground(Void... params) {
//        Subscription subscriptionService = SubscriptionFactory.getInstance();
//        List<SubscriptionRecord> subscriptionRecords = null;
//        try {
//            subscriptionRecords = subscriptionService.subscribed().execute().getItems();
//            Log.d(UserFragment.TAG, "Get subscription records count: " + subscriptionRecords.size());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return subscriptionRecords;
//    }
//
//    @Override
//    protected void onPostExecute(List<SubscriptionRecord> subscriptionRecords) {
//        super.onPostExecute(subscriptionRecords);
//        for (SubscriptionRecord record : subscriptionRecords) {
//            if (record.getIsAccepted())
//                notifySubscriptionConfirmed(record.getTarget());
//            else
//                notifySubscriptionSent(record.getTarget());
//        }
//        notifyChange();
//    }
//}
