package com.lms.questionbank;


import com.lms.course.CourseService;
import com.lms.questionbank.question.mcq.MCQ;
import com.lms.questionbank.question.Question;
import com.lms.questionbank.question.QuestionDTO;
import com.lms.questionbank.question.shortanswerquestion.ShortAnswerQuestion;
import com.lms.user.User;
import com.lms.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Collection;

@RestController
@PreAuthorize("hasAuthority('INSTRUCTOR')")
@RequestMapping("/courses/{courseId}/questions")
@AllArgsConstructor
public class QuestionBankController {
    private final QuestionBankService questionBankService;
    private final UserService userService;
    private final CourseService courseService;
    private boolean checkInstructor(Principal principal,@PathVariable long courseId) {
        User currentInstructor = userService.getUser(principal);
        if(courseService.getCourseById(courseId).isPresent())
            return courseService.getCourseById(courseId).get().getInstructor().getId() == currentInstructor.getId();
        return false;
    }

    @GetMapping("/{questionBankId}/question/{id}")
    public Question getQuestion(@PathVariable long questionBankId, @PathVariable long id, Principal principal,@PathVariable long courseId)
    {
        if(!(this.checkInstructor(principal,courseId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return questionBankService.getQuestion(id,questionBankId).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND,"Question not found"));
    }

    @GetMapping("/{questionBankId}")
    public Collection<Question> getAllQuestions(@PathVariable long questionBankId, Principal principal,@PathVariable long courseId)
    {
        if(!(this.checkInstructor(principal,courseId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return questionBankService.getAllQuestions(questionBankId);
    }

    @GetMapping("{questionBankId}/{discriminator}")
    public Collection<Question> getAllQuestionsOfType(@PathVariable long questionBankId, @PathVariable String discriminator, Principal principal,@PathVariable long courseId)
    {
        if(!(this.checkInstructor(principal,courseId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return this.questionBankService.getAllQuestionsOfType(discriminator,questionBankId);
    }


    @PostMapping
    public ResponseEntity<?> addQuestionBank(@RequestBody QuestionBank questionBank,@PathVariable long courseId, Principal principal)
    {
        if(!(this.checkInstructor(principal,courseId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        this.questionBankService.newQuestionBank(questionBank,courseId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{questionBankId}")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionDTO newQuestion, @PathVariable long questionBankId, Principal principal,@PathVariable long courseId)
    {
        if(!(this.checkInstructor(principal,courseId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        Question question;
        if(newQuestion.getQuestionType().equals("mcq"))
            question = MCQ.builder()
                    .option1(newQuestion.getOption1())
                    .option2(newQuestion.getOption2())
                    .option3(newQuestion.getOption3())
                    .option4(newQuestion.getOption4())
                    .correctOption(newQuestion.getCorrectOption())
                    .build();
        else
            question = ShortAnswerQuestion.builder()
                    .answer(newQuestion.getAnswer())
                    .build();

        question.setQuestionTitle(newQuestion.getQuestionTitle());
        this.questionBankService.addQuestion(question,questionBankId);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{questionBankId}/question/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable long questionBankId, @PathVariable long id, Principal principal,@PathVariable long courseId)
    {
        if(!(this.checkInstructor(principal,courseId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        this.questionBankService.deleteQuestion(id,questionBankId);
        return ResponseEntity.ok().build();
    }
}
