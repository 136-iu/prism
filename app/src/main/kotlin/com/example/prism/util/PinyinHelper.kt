package com.example.prism.util

import com.example.prism.data.Song

object PinyinHelper {

    private val map = mapOf(
        '阿' to 'A', '爱' to 'A', '安' to 'A', '岸' to 'A', '奥' to 'A',
        '白' to 'B', '北' to 'B', '不' to 'B', '别' to 'B', '本' to 'B', '边' to 'B',
        '陈' to 'C', '曾' to 'C', '春' to 'C', '从' to 'C', '长' to 'C', '城' to 'C',
        '大' to 'D', '的' to 'D', '冬' to 'D', '第' to 'D', '等' to 'D', '东' to 'D',
        '而' to 'E', '二' to 'E',
        '方' to 'F', '风' to 'F', '飞' to 'F', '福' to 'F', '分' to 'F',
        '高' to 'G', '过' to 'G', '光' to 'G', '关' to 'G', '感' to 'G',
        '海' to 'H', '后' to 'H', '红' to 'H', '黑' to 'H', '花' to 'H', '回' to 'H',
        '一' to 'Y', '月' to 'Y', '夜' to 'Y', '雨' to 'Y', '已' to 'Y', '有' to 'Y',
        '就' to 'J', '今' to 'J', '记' to 'J', '家' to 'J', '就' to 'J', '见' to 'J',
        '看' to 'K', '开' to 'K', '空' to 'K', '可' to 'K',
        '了' to 'L', '来' to 'L', '离' to 'L', '两' to 'L', '里' to 'L', '蓝' to 'L',
        '们' to 'M', '明' to 'M', '梦' to 'M', '每' to 'M', '美' to 'M', '没' to 'M',
        '你' to 'N', '那' to 'N', '年' to 'N', '内' to 'N', '南' to 'N', '能' to 'N',
        '哦' to 'O', '偶' to 'O',
        '片' to 'P', '漂' to 'P', '陪' to 'P',
        '去' to 'Q', '前' to 'Q', '情' to 'Q', '请' to 'Q', '青' to 'Q', '其' to 'Q',
        '人' to 'R', '日' to 'R', '如' to 'R', '让' to 'R',
        '是' to 'S', '三' to 'S', '岁' to 'S', '时' to 'S', '山' to 'S', '水' to 'S',
        '他' to 'T', '她' to 'T', '天' to 'T', '同' to 'T', '听' to 'T', '太' to 'T',
        '无' to 'W', '我' to 'W', '为' to 'W', '未' to 'W', '忘' to 'W', '温' to 'W',
        '下' to 'X', '小' to 'X', '心' to 'X', '新' to 'X', '想' to 'X', '相' to 'X',
        '也' to 'Y', '又' to 'Y', '已' to 'Y', '眼' to 'Y', '远' to 'Y', '云' to 'Y',
        '在' to 'Z', '中' to 'Z', '之' to 'Z', '自' to 'Z', '这' to 'Z', '只' to 'Z'
    )

    fun firstLetter(text: String): Char {
        if (text.isBlank()) return '#'
        val c = text[0]
        return when {
            c in 'A'..'Z' -> c
            c in 'a'..'z' -> c.uppercaseChar()
            c in '\u4e00'..'\u9fff' -> map[c] ?: '#'
            else -> '#'
        }
    }

    fun groupByLetter(songs: List<Song>): Map<Char, List<Song>> {
        return songs.groupBy { firstLetter(it.title) }.toSortedMap()
    }

    fun indexBar(): List<Char> = ('A'..'Z').toList() + '#'
}