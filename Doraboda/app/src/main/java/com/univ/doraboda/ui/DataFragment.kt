package com.univ.doraboda.ui

import android.app.AlertDialog
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.univ.doraboda.R
import com.univ.doraboda.databinding.DialogCalendardatepickerBinding
import com.univ.doraboda.databinding.FragmentDataBinding
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.viewModel.CalendarViewModel
import com.univ.doraboda.viewModel.CalendarViewModel.CalendarIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class DataFragment : BaseFragment<FragmentDataBinding>() {
    val viewModel: CalendarViewModel by viewModels()
    lateinit var emotionNumberList: MutableList<Int> //감정 분포도
    var maximumNumber = 0
    lateinit var selectedCalendarItem: Calendar
    val emotionImageList = listOf(R.drawable.normal, R.drawable.sad, R.drawable.joyful, R.drawable.angry, R.drawable.confused, R.drawable.happy, R.drawable.icon_quetionmark)
    val emotionTextList = listOf("무감각", "슬픔", "즐거움", "분노", "혼란", "행복")
    var maximumIndexList = mutableListOf<Int>()
    override fun layoutId() = R.layout.fragment_data

    override fun layoutInit() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect{
                    if(it.isLoading){

                    } else {
                        if(it.isError){

                        } else {
                            val maximumEmotionIndex = getMaximumEmotionAndEmotionData(it.emotions)
                            if (maximumIndexList.size == 0){
                                binding.dataBarChartImageView.visibility = View.VISIBLE
                                binding.dataPieChartImageView.visibility = View.VISIBLE
                            } else {
                                binding.dataBarChartImageView.visibility = View.INVISIBLE
                                binding.dataPieChartImageView.visibility = View.INVISIBLE
                            }
                            binding.dataTextView4.text = when (maximumIndexList.size) {
                                0 -> "아직 감정 데이터가 없어요"
                                1 -> "이번달, 가장 많이 느낀 감정은 ${emotionTextList.get(maximumEmotionIndex)}"
                                2 -> "이번달, 가장 많이 느낀 감정은 ${emotionTextList.get(maximumIndexList.get(0))}, ${emotionTextList.get(maximumIndexList.get(1))}"
                                else -> "다양한 감정을 골고루 느끼셨어요"
                            }

                            val values = ArrayList<BarEntry>()
                            for (i in 0..<6) {
                                values.add(
                                    BarEntry(
                                        (5 - i).toFloat(),
                                        emotionNumberList.get(i).toFloat()
                                    )
                                )
                            }
                            val dataSet = BarDataSet(values, "")
                            dataSet.setValueTextSize(15f)
                            dataSet.valueFormatter = object : ValueFormatter() {
                                override fun getBarLabel(barEntry: BarEntry): String {
                                    return "${barEntry.y.toInt()}"
                                }
                            }
                            val colorList = mutableListOf<Int>()
                            colorList.add(
                                0,
                                ContextCompat.getColor(requireContext(), R.color.lightGrey)
                            )
                            colorList.add(1, ContextCompat.getColor(requireContext(), R.color.blue))
                            colorList.add(2, ContextCompat.getColor(requireContext(), R.color.orange))
                            colorList.add(3, ContextCompat.getColor(requireContext(), R.color.red))
                            colorList.add(
                                4,
                                ContextCompat.getColor(requireContext(), R.color.purple_200)
                            )
                            colorList.add(
                                5,
                                ContextCompat.getColor(requireContext(), R.color.mainYellow)
                            )

                            dataSet.setColors(colorList)
                            val barData = BarData(dataSet)

                            binding.dataBarChart.apply {
                                axisLeft.axisMaximum = maximumNumber.toFloat()
                                data = barData
                                invalidate()
                            }

                            val values2 = ArrayList<PieEntry>()
                            for (i in 0..<6) {
                                values2.add(
                                    PieEntry(
                                        emotionNumberList.get(i).toFloat(),
                                        if (emotionNumberList.get(i) != 0) emotionTextList.get(i) else ""
                                    )
                                )
                            }
                            val dataSet2 = PieDataSet(values2, "").apply {
                                setColors(colorList)
                            }
                            val pieData = PieData(dataSet2).apply {
                                setValueFormatter(object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String? {
                                        return if (value == 0f) "" else "${value.toInt()}%"
                                    }
                                })
                                setValueTextSize(20f)
                                setValueTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.white
                                    )
                                )
                            }

                            binding.dataPieChart.apply {
                                data = pieData
                                invalidate()
                            }
                            Glide.with(requireContext()).load(emotionImageList.get(maximumEmotionIndex)).into(binding.dataImageView)
                        }
                    }
                }
            }
        }
        val calendar = Calendar.getInstance()
        selectedCalendarItem = calendar.clone() as Calendar
        val calendar1 = Calendar.getInstance()
        calendar1.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0)
        val calendar2 = Calendar.getInstance()
        calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DATE), 0, 0, 0)
        binding.dataTextView2.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH)+1}월"

        binding.dataBarChart.apply {
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            xAxis.setDrawAxisLine(false)
            axisLeft.setDrawAxisLine(false)
            axisRight.setDrawAxisLine(false)

            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)

            axisLeft.setDrawLabels(false)
            axisRight.setDrawLabels(false)

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
            xAxis.textSize = 15f
            xAxis.valueFormatter = object : ValueFormatter(){
                override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
                    return "${emotionTextList.get(5-value.toInt())}"
                }
            }
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.axisMinimum = 0f
            setTouchEnabled(false)
        }

        binding.dataPieChart.apply {
            holeRadius = 50f
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setUsePercentValues(true)
            setEntryLabelTextSize(15f)
        }
        viewModel.handleIntent(CalendarIntent.LoadBetweenMemoAndEmotion(calendar1.timeInMillis, calendar2.timeInMillis))
        binding.dataTextView2.setOnClickListener {
            val dialogBinding = DialogCalendardatepickerBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogBinding.root)
            val dialog = builder.create()
            dialogBinding.yearNumberPicker.apply {
                minValue = 2000
                maxValue = 3000
                value = selectedCalendarItem.get(Calendar.YEAR)
            }
            dialogBinding.monthNumberPicker.apply {
                minValue = 1
                maxValue = 12
                value = selectedCalendarItem.get(Calendar.MONTH) + 1
            }
            dialogBinding.calendarDatePickerCancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.calendarDatePickerDoneButton.setOnClickListener {
                //현재 선택된 캘린더 데이터 갱신
                selectedCalendarItem.set(Calendar.YEAR, dialogBinding.yearNumberPicker.value)
                selectedCalendarItem.set(Calendar.MONTH, dialogBinding.monthNumberPicker.value - 1)
                val calendar3 = Calendar.getInstance()
                calendar3.set(selectedCalendarItem.get(Calendar.YEAR), selectedCalendarItem.get(Calendar.MONTH), 1, 0, 0, 0)
                val calendar4 = Calendar.getInstance()
                calendar4.set(selectedCalendarItem.get(Calendar.YEAR), selectedCalendarItem.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DATE), 0, 0, 0)
                viewModel.handleIntent(CalendarIntent.LoadBetweenMemoAndEmotion(calendar3.timeInMillis, calendar4.timeInMillis))
                binding.dataTextView2.text = "${dialogBinding.yearNumberPicker.value}년 ${dialogBinding.monthNumberPicker.value}월"
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun getMaximumEmotionAndEmotionData(emotions: List<Emotion>?): Int{
        emotionNumberList = mutableListOf(0, 0, 0, 0, 0, 0)
        maximumNumber = 0
        maximumIndexList = mutableListOf<Int>()

        for(i in 0..<emotions!!.size){
            val thisEmotion: Int =
                when(emotions.get(i).emotion){
                "normal" -> 0
                "sad" -> 1
                "joyful" -> 2
                "angry" -> 3
                "confused" -> 4
                "happy" -> 5
                    else -> 6 //indexOutOfBounds
                }
            emotionNumberList.set(thisEmotion, emotionNumberList.get(thisEmotion)+1)
        }
        var maximumEmotionIndex = 6

        for(i in 0..<6){
            val thisEmotionNumber = emotionNumberList.get(i)
            if(thisEmotionNumber > maximumNumber){
                maximumEmotionIndex = i
                maximumNumber = thisEmotionNumber
            }
        }
        if(maximumNumber != 0)
        for(i in 0..<6){
            val thisEmotionNumber = emotionNumberList.get(i)
            if (thisEmotionNumber == maximumNumber){
                maximumIndexList.add(i)
            }
        }
        return maximumEmotionIndex
    }
}