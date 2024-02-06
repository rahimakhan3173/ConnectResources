package com.smallworldfs.coding_test.connectResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConnectResourceTest {
    public static void main(String[] args) throws IOException {
        ConnectResourceTest t = new ConnectResourceTest();
        t.PianoKeyboard(new File("pianoInput.json"), -3);

    }
    public void PianoKeyboard(File file, int semitone) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<Integer>> inputNotes = objectMapper.readValue(file, new TypeReference<List<List<Integer>>>() {});
        List<List<Integer>> outputNotes = new ArrayList<>();
        List<Integer> noteList = getNoteList();
        int size = noteList.size(), newNote = 0, octaveNumber = 0, noteNumber = 0, newNoteIndex = 0;
        boolean outOfRange = false;

        log.info("Input Notes {}", inputNotes);

        for (List<Integer> note : inputNotes) {
            octaveNumber = note.get(0);
            noteNumber = note.get(1);

            int index = circularIndexOf(noteList, noteNumber); // get index of current note from note List

            if (index == -1) {  // note falls from keyboard range
                outOfRange = true;
                break;
            }
            
//            calculate new note from note list
            newNoteIndex = (index + semitone + size) % size;
            
//            if semitone positive move right side
            if (newNoteIndex > index && semitone < 1) {
                octaveNumber = octaveNumber - 1;
                
            } else if (newNoteIndex < index && semitone > 0) { 
//                if semitone negative move left side
                octaveNumber = octaveNumber + 1;
            }
            
//            octave falls from keyboard range
            if (octaveNumber < 1 || octaveNumber > 8){ 
                outOfRange = true;
                break;
            }
//            get new note from note list 
            newNote = noteList.get(newNoteIndex);

            List<Integer> outputNote = new ArrayList<>();
            outputNote.add(octaveNumber);
            outputNote.add(newNote);
            outputNotes.add(outputNote);
        }
        if (outOfRange) {
            log.error("one of the resulting notes falls out of the keyboard range");
        } else {
            log.info("Output Notes {}", outputNotes);
            objectMapper.writeValue(new File("transposed_notes.json"), outputNotes);
            log.info("Output notes are save in transposed_notes.json file ");
        }
    }
//    generate note list of single octave 
    private List<Integer> getNoteList() {
        List<Integer> noteList = new ArrayList<>();
        for (int i = 1 ; i <13; i++){
            noteList.add(i);
        }
        return noteList;
    }

//    get index of note given as input 
    public int circularIndexOf(List<Integer> list, int target) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (list.get(i) == target) {

                return i;
            }
        }
        return -1; // Element not found
    }
}
